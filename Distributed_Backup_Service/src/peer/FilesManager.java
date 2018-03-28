package peer;

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

import utils.Chunk;
import utils.FileInfo;
import utils.Utils;

public class FilesManager {

	private int ownerId;
	private int currentDiskSpace = Utils.MAX_DISK_SPACE;
	private ArrayList<FileInfo> peerFiles = new ArrayList<FileInfo>();
	private ArrayList<Chunk> peerChunks = new ArrayList<Chunk>();

	public FilesManager(int ownerId) {
		this.ownerId = ownerId;
		loadDirectory();
	}

	public void loadDirectory() {
		createDirectories();
		readInfoDirectory();
		readFilesDirectory();
	}

	public void createDirectories() {
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

	public void readInfoDirectory() {
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
				ArrayList<FileInfo> readCase = (ArrayList<FileInfo>) objectinputstream.readObject();
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
				ArrayList<Chunk> readCase = (ArrayList<Chunk>) objectinputstream.readObject();
				peerChunks.addAll(readCase);
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

	public void readFilesDirectory() {
		File filesDir = new File(this.getFilesDir());
		if (filesDir.exists() && filesDir.isDirectory()) {
			for (File file : filesDir.listFiles()) {
				boolean backedUp = false;
				try {
					String encryptedId = encryptFileId(file);

					for (FileInfo fInfo : this.peerFiles) {
						if (fInfo.getId().equals(encryptedId)) {
							backedUp = true;
							break;
						}
					}
					if (!backedUp) {
						int chunksQuantity = (int) (file.length() / Utils.MAX_CHUNK_SIZE) + 1;
						peerFiles.add(new FileInfo(encryptedId, file.getName(), false, chunksQuantity, -1));
					}

				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void saveInfo() {
		saveFilesInfo();
		saveChunksInfo();
	}

	public void saveFilesInfo() {
		ObjectOutputStream oosFiles = null;
		FileOutputStream foutFiles = null;

		try {
			foutFiles = new FileOutputStream(this.getFilesInfoFile(), false);
			oosFiles = new ObjectOutputStream(foutFiles);
			oosFiles.writeObject(this.getBackedUpFiles());
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
			oosChunks.writeObject(this.peerChunks);
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

	public ArrayList<Chunk> splitToChunks(File file, int replicationDegree) {

		ArrayList<Chunk> chunkList = new ArrayList<Chunk>();

		if (file.exists()) {
			try {
				String encryptedId = encryptFileId(file);
				int chunksQuantity = (int) (file.length() / Utils.MAX_CHUNK_SIZE) + 1;
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

					chunkList.add(new Chunk(encryptedId, chunkNo, replicationDegree, chunkData, this.ownerId));
				}

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return chunkList;
	}

	public boolean canSaveChunk(Chunk chunk) {
		if (chunk.getData().length > currentDiskSpace) {
			System.out.println("Insufficient disk space.");
			return false;
		}
		for (Chunk peerChunk : peerChunks) {
			if ((peerChunk.getFileId() + peerChunk.getChunkNo()).equals(chunk.getFileId() + chunk.getChunkNo())) {
				System.out.println("Chunk already backed up.");
				return false;
			}
		}
		return true;
	}

	public void saveChunk(Chunk chunk) {
		byte data[] = chunk.getData();
		try {
			FileOutputStream out = new FileOutputStream(getChunksDir() + "\\" + chunk.getFileId() + chunk.getChunkNo());
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			this.peerChunks.add(chunk);
		}
	}

	public boolean repeatedChunk(Chunk chunk) {
		for (Chunk peerChunk : this.peerChunks) {
			if ((peerChunk.getFileId() + peerChunk.getChunkNo()).equals(chunk.getFileId() + chunk.getChunkNo())) {
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
		return System.getProperty("user.dir") + "\\Peer" + this.ownerId + "disk";
	}

	public String getFilesDir() {
		return System.getProperty("user.dir") + "\\Peer" + this.ownerId + "disk" + "\\Files";
	}

	public String getChunksDir() {
		return System.getProperty("user.dir") + "\\Peer" + this.ownerId + "disk" + "\\Chunks";
	}

	public String getInfoDir() {
		return System.getProperty("user.dir") + "\\Peer" + this.ownerId + "disk" + "\\Info";
	}

	public String getFilesInfoFile() {
		return System.getProperty("user.dir") + "\\Peer" + this.ownerId + "disk" + "\\Info\\backedUpFiles.ser";
	}

	public String getChunksInfoFile() {
		return System.getProperty("user.dir") + "\\Peer" + this.ownerId + "disk" + "\\Info\\chunksInfo.ser";
	}

	public ArrayList<FileInfo> getBackedUpFiles() {
		ArrayList<FileInfo> backedUpFiles = new ArrayList<FileInfo>();
		for (FileInfo file : this.peerFiles) {
			if (file.isBackedUp()) {
				backedUpFiles.add(file);
			}
		}
		return backedUpFiles;
	}

	public FileInfo getFileInfo(String fileName) {
		for (FileInfo file : this.peerFiles) {
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

	public void updateBackedUpFiles(FileInfo fileInfo) {
		this.peerFiles.remove(fileInfo);
		this.peerFiles.add(fileInfo);
		return;
	}

}
