package initiators;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import peer.Peer;
import protocols.ChunkBackupProtocol;
import utils.Chunk;
import utils.FileInfo;

public class BackupInitiator implements Runnable {

	private ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	private Peer peer = null;
	private String fileName = null;
	private int replicationDegree = 0;

	public BackupInitiator(Peer peer, String fileName, int replicationDegree) {
		this.peer = peer;
		this.fileName = fileName;
		this.replicationDegree = replicationDegree;
	}

	@Override
	public void run() {
		FileInfo fileInfo = peer.getFilesManager().getFileInfo(this.fileName);
		if (fileInfo == null) {
			System.out.println("File doesn't exist!");
			return;
		}

		File existingFile = peer.getFilesManager().getExistingFile(fileName);
		if (existingFile == null) {
			System.out.println("Program integrity violation. File doesn't exist. Please restart the program.");
			return;
		}
		String encryptedFileId = null;
		try {
			encryptedFileId = peer.getFilesManager().encryptFileId(existingFile);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		if (fileInfo.isBackedUp()) {
			if (fileInfo.getId().equals(encryptedFileId)) {
				System.out.println("File is already backed up!");
				return;
			} else {
				// file is a modification -> delete protocol
				System.out.println("Modified file, deleting existing copies and backing up new one.");
			}
		}

		ArrayList<Chunk> chunks = this.peer.getFilesManager().splitToChunks(existingFile, replicationDegree);
		ArrayList<Thread> protocolThreads = new ArrayList<Thread>();

		for (Chunk chunk : chunks) {
			Thread thread = new Thread(new ChunkBackupProtocol(this.peer, chunk, replicationDegree));
			protocolThreads.add(thread);
			this.peer.getStoredMessages().clear();
			thread.start();
		}

		for (Thread thread : protocolThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for(Chunk chunk : chunks) {
			if(chunk.getOwnerIds().size() > 0) {
				
				FileInfo newBackedUpFile = new FileInfo(encryptedFileId, fileName, true);
				newBackedUpFile.getBackedUpChunks().addAll(chunks);
				this.peer.getFilesManager().updateBackedUpFiles(newBackedUpFile);
				this.peer.getFilesManager().saveInfo();
				
				if(chunk.getOwnerIds().size() >= this.replicationDegree) {
					System.out.println("Successful backup of chunk "+ chunk.getChunkNo());
				} else {
					System.out.println("Successful backup of chunk " + chunk.getChunkNo() +" but with a replication degree below the threshold");
				}
				System.out.println("Desired replication degree: " + this.replicationDegree);
				System.out.println("Current replication degree: " + chunk.getOwnerIds().size());
			} else {
				System.out.println("Unsuccessful backup of chunk " + chunk.getChunkNo());
			}
		}
		return;
	}

}
