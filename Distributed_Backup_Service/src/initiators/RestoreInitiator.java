package initiators;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import filesmanager.Chunk;
import peer.Peer;
import protocols.ChunkBackupProtocol;
import protocols.ChunkRestoreProtocol;
import utils.Utils;

public class RestoreInitiator implements Runnable {
	
	private Peer peer = null;
	private String fileName = null;

	public RestoreInitiator(Peer peer, String fileName) {
		this.peer = peer;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		if (peer.getFilesManager().checkIfFileExists(this.fileName)) {
			System.out.println("File already backed up!");
			return;
		}
		
		File existingFile = peer.getFilesManager().getExistingFile(this.fileName);
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

		ArrayList<Chunk> fileChunks = new ArrayList<Chunk>();
		ArrayList<Thread> protocolThreads = new ArrayList<Thread>();
		ArrayList<ChunkRestoreProtocol> threadTasks = new ArrayList<ChunkRestoreProtocol>();
		int chunksQuantity = Utils.calcChunksQuantity(existingFile);
		
		for(int i = 0; i < chunksQuantity; i++) {
			ChunkRestoreProtocol task = new ChunkRestoreProtocol(this.peer);
			threadTasks.add(task);
			Thread thread = new Thread(task);
			protocolThreads.add(thread);
		}
		
		for (int i = 0; i < protocolThreads.size(); i++) {
			try {
				protocolThreads.get(i).join();
				Chunk chunk = threadTasks.get(i).getChunk();
				if(chunk == null) {
					System.out.println("Unsuccessful restoration of chunk " + i);
					System.out.println("Ending restoration process.");
					return;
				} else {
					fileChunks.add(chunk);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.peer.getFilesManager().restoreFile(this.fileName, fileChunks);
		
	}

}
