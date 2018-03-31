package initiators;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import filesmanager.ChunkInfo;
import peer.Peer;
import protocols.ChunkBackupProtocol;

public class BackupInitiator implements Runnable {

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
		BackedUpFileInfo fileInfo = peer.getFilesManager().getFileInfo(this.fileName);
		if (fileInfo == null) {
			System.out.println("File doesn't exist!");
			return;
		}

		File existingFile = peer.getFilesManager().getExistingFile(fileName);
		String encryptedFileId = null;
		if (existingFile == null) {
			System.out.println("Program integrity violation. File doesn't exist. Please restart the program.");
			return;
		}
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

		ArrayList<Chunk> chunks = this.peer.getFilesManager().splitToChunks(existingFile);
		ArrayList<ChunkInfo> chunksInfo = new ArrayList<ChunkInfo>();
		ArrayList<Thread> protocolThreads = new ArrayList<Thread>();

		for (int i = 0; i < chunks.size(); i++) {
			ChunkInfo chunkInfo = new ChunkInfo(encryptedFileId, i, replicationDegree);
			chunksInfo.add(chunkInfo);
			Thread thread = new Thread(new ChunkBackupProtocol(this.peer, chunkInfo, chunks.get(i).getData() ));
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
		
		for(ChunkInfo chunkInfo: chunksInfo) {
			if(chunkInfo.getOwnerIds().size() > 0) {
				
				BackedUpFileInfo newBackedUpFile = new BackedUpFileInfo(encryptedFileId, fileName, existingFile.lastModified(), true);
				newBackedUpFile.getBackedUpChunks().addAll(chunksInfo);
				this.peer.getFilesManager().updateBackedUpFiles(newBackedUpFile);
				this.peer.getFilesManager().saveFilesInfo();
				
				if(chunkInfo.getOwnerIds().size() >= this.replicationDegree) {
					System.out.println("Successful backup of chunk "+ chunkInfo.getChunkNo());
				} else {
					System.out.println("Successful backup of chunk " + chunkInfo.getChunkNo() +" but with a replication degree below the threshold");
				}
				System.out.println("Desired replication degree: " + this.replicationDegree);
				System.out.println("Current replication degree: " + chunkInfo.getOwnerIds().size());
			} else {
				System.out.println("Unsuccessful backup of chunk " + chunkInfo.getChunkNo());
			}
		}
		return;
	}

}
