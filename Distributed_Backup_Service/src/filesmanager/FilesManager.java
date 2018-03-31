package filesmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;

import communications.Message;
import utils.Utils;

public class FilesManager {

	private int ownerId;
	private int currentDiskSpace = Utils.MAX_DISK_SPACE;
	private ArrayList<BackedUpFileInfo> peerFiles = new ArrayList<BackedUpFileInfo>();
	private ArrayList<ChunkInfo> peerChunksInfo = new ArrayList<ChunkInfo>();
	private ArrayList<Chunk> chunksToSave = new ArrayList<Chunk>();

	public FilesManager(int ownerId) {
		this.ownerId = ownerId;
		loadDirectory();
	}

	public void loadDirectory() {
		checkSharedDirectories();
		createPeerDirectories();
		readInfoDirectory();
		readFilesDirectory();
	}

	private void checkSharedDirectories() {
		File[] sharedDirs = new File[] {new File("Peers"), new File("SharedFiles")};
		for(File dir : sharedDirs) {
			if(!dir.exists()) {
				dir.mkdir();
			}
		}
	}

	private void createPeerDirectories() {
		File[] dirs = new File[] { new File(getDiskDir()), new File(getFilesDir()), new File(getChunksDir()),
				new File(getInfoDir()) };
		for (File dir : dirs) {
			if (!dir.exists()) {
				try {
					dir.mkdir();
				} catch (SecurityException se) {
					se.printStackTrace();
				}
			} else {
				currentDiskSpace -= dir.length();
			}
		}
	}

	private void readInfoDirectory() {
		readBackedUpFilesInfo();
		readChunksInfo();
	}

	private void readBackedUpFilesInfo() {
		String backedUpFilesPath = this.getFilesInfoFile();
		File backedUpFiles = new File(backedUpFilesPath);
		if (backedUpFiles.exists()) {
			ObjectInputStream objectinputstream = null;
			try {
				FileInputStream streamIn = new FileInputStream(backedUpFilesPath);
				objectinputstream = new ObjectInputStream(streamIn);
				@SuppressWarnings("unchecked")
				ArrayList<BackedUpFileInfo> readCase = (ArrayList<BackedUpFileInfo>) objectinputstream.readObject();
				peerFiles.addAll(readCase);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (objectinputstream != null) {
					try {
						objectinputstream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void readChunksInfo() {
		String chunksInfoPath = this.getChunksInfoFile();
		File chunks = new File(chunksInfoPath);
		if (chunks.exists()) {
			ObjectInputStream objectinputstream = null;
			try {
				FileInputStream streamIn = new FileInputStream(chunksInfoPath);
				objectinputstream = new ObjectInputStream(streamIn);
				@SuppressWarnings("unchecked")
				ArrayList<ChunkInfo> readCase = (ArrayList<ChunkInfo>) objectinputstream.readObject();
				peerChunksInfo.addAll(readCase);
				System.out.println("PEER CHUNKS INFO " + peerChunksInfo.size());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (objectinputstream != null) {
					try {
						objectinputstream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void readFilesDirectory() {
		File filesDir = new File(this.getFilesDir());
		if (filesDir.exists() && filesDir.isDirectory()) {
			for (File file : filesDir.listFiles()) {
				boolean backedUp = false;
				try {
					String encryptedId = encryptFileId(file);
					for (BackedUpFileInfo fInfo : this.peerFiles) {
						if (fInfo.getId().equals(encryptedId)) {
							backedUp = true;
							break;
						}
					}
					if (!backedUp) {
						peerFiles.add(new BackedUpFileInfo(encryptedId, file.getName(), file.lastModified(), false));
					}

				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveFilesInfo() {
		ObjectOutputStream oosFiles = null;
		FileOutputStream foutFiles = null;

		try {
			foutFiles = new FileOutputStream(this.getFilesInfoFile(), false);
			oosFiles = new ObjectOutputStream(foutFiles);
			oosFiles.writeObject(this.peerFiles);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (oosFiles != null) {
				try {
					oosFiles.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public void saveChunksInfo() {
		ObjectOutputStream oosChunks = null;
		FileOutputStream foutChunks = null;

		try {
			foutChunks = new FileOutputStream(this.getChunksInfoFile(), false);
			oosChunks = new ObjectOutputStream(foutChunks);
			oosChunks.writeObject(this.peerChunksInfo);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (oosChunks != null) {
				try {
					oosChunks.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<Chunk> splitToChunks(File file) {

		ArrayList<Chunk> chunkList = new ArrayList<Chunk>();

		if (file.exists()) {
			try {
				String encryptedId = encryptFileId(file);
				int chunksQuantity = Utils.calcChunksQuantity(file);
				byte[] fileBytes = Files.readAllBytes(file.toPath());

				for (int chunkNo = 0; chunkNo < chunksQuantity; chunkNo++) {

					int chunkSize = Utils.MAX_CHUNK_SIZE;

					if (chunkNo == chunksQuantity - 1) {
						chunkSize = fileBytes.length % Utils.MAX_CHUNK_SIZE;
					}

					// copy file data to chunk chunkList
					int start = chunkNo * Utils.MAX_CHUNK_SIZE;
					byte[] chunkData = new byte[chunkSize];
					int byteCounter = 0;

					for (int j = start; j < start + chunkSize; j++) {
						chunkData[byteCounter] = fileBytes[j];
						byteCounter++;
					}

					chunkList.add(new Chunk(encryptedId + chunkNo, chunkData));
				}

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return chunkList;
	}

	public boolean canSaveData(int dataLength) {
		if (dataLength > currentDiskSpace) {
			System.out.println("Insufficient disk space.");
			return false;
		}
		return true;
	}

	public boolean hasChunk(ChunkInfo chunk) {
		for (ChunkInfo peerChunk : peerChunksInfo) {
			if ((peerChunk.getFileId() + peerChunk.getChunkNo()).equals(chunk.getFileId() + chunk.getChunkNo())) {
				return true;
			}
		}
		return false;
	}

	public void updateChunkOwners(Message message) {
		for (ChunkInfo peerChunk : peerChunksInfo) {
			if ((message.getFileId() + message.getChunkNo()).equals(peerChunk.getFileId() + peerChunk.getChunkNo())
					&& !peerChunk.getOwnerIds().contains(message.getSenderId())) {
				peerChunk.getOwnerIds().add(message.getSenderId());
				return;
			}
		}
		return;
	}

	public void saveAllChunks() {
		for(Chunk chunk : this.chunksToSave) {
			this.saveChunk(chunk);
		}
	}
	
	public void addChunkToSave(Chunk chunk) {
		this.chunksToSave.add(chunk);
	}
	
	public void saveChunk(Chunk chunk) {
		byte data[] = chunk.getData();
		try {
			FileOutputStream out = new FileOutputStream(getChunksDir() + Utils.getSeparator() + chunk.getChunkId());
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveFile(String fileName, byte[] data) {
		new File(this.getPrivateFilesDir() + Utils.getSeparator() + fileName);
		try {
			FileOutputStream out = new FileOutputStream(this.getPrivateFilesDir() + Utils.getSeparator() + fileName);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean repeatedChunk(ChunkInfo chunkInfo) {
		for (ChunkInfo peerChunkInfo : this.peerChunksInfo) {
			if ((peerChunkInfo.getFileId() + peerChunkInfo.getChunkNo()).equals(chunkInfo.getFileId() + chunkInfo.getChunkNo())) {
				return true;
			}
		}
		return false;
	}

	public String encryptFileId(File file) throws NoSuchAlgorithmException {
		String temp = file.getName() + file.lastModified() + ownerId;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(temp.getBytes(StandardCharsets.UTF_8));

		return DatatypeConverter.printHexBinary(hash);
	}

	public String getDiskDir() {
		return System.getProperty("user.dir") + Utils.getSeparator() + "Peers"+ Utils.getSeparator() +"Peer" + this.ownerId + "disk";
	}
	
	public String getPrivateFilesDir() {
		return System.getProperty("user.dir") + Utils.getSeparator() + "SharedFiles";
	}


	public String getFilesDir() {
		return System.getProperty("user.dir") + Utils.getSeparator() + "SharedFiles";
	}

	public String getChunksDir() {
		return System.getProperty("user.dir") + Utils.getSeparator() + "Peers" + Utils.getSeparator() + "Peer" + this.ownerId + "disk" + Utils.getSeparator() + "Chunks";
	}

	public String getInfoDir() {
		return System.getProperty("user.dir") + Utils.getSeparator() + "Peers" + Utils.getSeparator() + "Peer" + this.ownerId + "disk" + Utils.getSeparator() + "Info";
	}

	public String getFilesInfoFile() {
		return System.getProperty("user.dir") + Utils.getSeparator() + "Peers" + Utils.getSeparator() + "Peer" + this.ownerId + "disk" + Utils.getSeparator() + "Info"+ Utils.getSeparator() + "backedUpFiles.ser";
	}

	public String getChunksInfoFile() {
		return System.getProperty("user.dir") + Utils.getSeparator() + "Peers" + Utils.getSeparator() + "Peer" + this.ownerId + "disk" + Utils.getSeparator() + "Info"+ Utils.getSeparator() + "chunksInfo.ser";
	}

	public ArrayList<BackedUpFileInfo> getBackedUpFiles() {
		ArrayList<BackedUpFileInfo> backedUpFiles = new ArrayList<BackedUpFileInfo>();
		for (BackedUpFileInfo file : this.peerFiles) {
			if (file.isBackedUp()) {
				backedUpFiles.add(file);
			}
		}
		return backedUpFiles;
	}

	public BackedUpFileInfo getFileInfo(String fileName) {
		for (BackedUpFileInfo file : this.peerFiles) {
			if (file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}

	public File getExistingFile(String fileName) {
		for (File file : new File(this.getFilesDir()).listFiles()) {
			if (file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}
	
	public File getExistingChunk(String fileName) {
		for (File file : new File(this.getChunksDir()).listFiles()) {
			if (file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}

	public void updateBackedUpFiles(BackedUpFileInfo fileInfo) {
		this.peerFiles.remove(fileInfo);
		this.peerFiles.add(fileInfo);
		return;
	}

	public ArrayList<ChunkInfo> getChunksInfo() {
		return this.peerChunksInfo;
	}

	public boolean checkIfFileExists(String fileName) {
		File restoredFiles = new File(this.getPrivateFilesDir());
		if (restoredFiles.exists() && restoredFiles.isDirectory()) {
			for(File file : restoredFiles.listFiles()) {
				if(file.getName().equals(fileName))
					return true;
			}
		} else {
			restoredFiles.mkdir();
		}
		return false;
	}
	
	public long getPrivateFileSize(String fileName) {
		File restoredFiles = new File(this.getPrivateFilesDir());
		if (restoredFiles.exists() && restoredFiles.isDirectory()) {
			for(File file : restoredFiles.listFiles()) {
				if(file.getName().equals(fileName))
					return file.length();
			}
		}
		return 0;
	}

	public void restoreFile(String fileName, ArrayList<Chunk> fileChunks) {

		/*
		FileOutputStream out = new FileOutputStream();

		for (int i = 0; i < fileChunks.size(); i++) 
		{
				byte data[] = .get();
				out.write(data);
		}
		out.close();
		*/
	}

}
