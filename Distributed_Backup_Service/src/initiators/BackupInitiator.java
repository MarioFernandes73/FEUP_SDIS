package initiators;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import peer.Peer;
import protocols.ChunkBackupProtocol;
import utils.Chunk;
import utils.FileInfo;
import utils.Message;
import utils.Utils;

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
		System.out.println("STARTED BACKUP");
		FileInfo fileInfo = peer.getFilesManager().getFileInfo(this.fileName);
		if(fileInfo == null) {
			System.out.println("File doesn't exist!");
			return;
		}
		
		File existingFile = peer.getFilesManager().getExistingFile(fileName);
		if(existingFile == null) {
			System.out.println("Program integrity violation. File doesn't exist. Please restart the program.");
			return;
		}
		
		String ecryptedExistingFileId = null;
		
		//file exists
		if(fileInfo.isBackedUp()) {
			try {
				ecryptedExistingFileId = peer.getFilesManager().encryptFileId(existingFile);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
			if(fileInfo.getId().equals(ecryptedExistingFileId)) {
				System.out.println("File is already backed up!");
				return;
			} else {
				//file is a modification -> delete protocol
				System.out.println("Modified file, deleting existing copies and backing up new one.");
			}
		}
		
		ArrayList<Chunk> chunks = this.peer.getFilesManager().splitToChunks(existingFile, replicationDegree);
		ArrayList<Thread> protocolThreads = new ArrayList<Thread>();
		
		for(Chunk chunk : chunks) {
			//construir mensagem PUTCHUNK
			Message message = new Message();
			message.setOperation("PUTCHUNK");
			message.setVersion(Utils.DEFAULT_VERSION);
			message.setSenderId(peer.getId());
			message.setFileId(fileInfo.getId());
			message.setChunkNo(chunk.getChunkNo());
			message.setReplicationDeg(this.replicationDegree);
			message.setBody(new String(chunk.getData(), StandardCharsets.UTF_8));
			
			//mandar mensagem PUTCHUNK usando o protocolo
			Thread thread = new Thread(new ChunkBackupProtocol(this.peer.getMDBChannel(), message, chunk.getOwnerIds()));
			protocolThreads.add(thread);
			thread.start();
		}
		
		for(Thread thread : protocolThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Updating backed up files.");
		//peer.getFilesManager().updateBackedUpFiles(new FileInfo(ecryptedExistingFileId, fileName, true, chunks.size() ,replicationDegree));
		//peer.getFilesManager().saveInfo();
		System.out.println("Successfull backup!");
		return;
	}
	
}
