package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;

public class FilesManager {

	private int ownerId;

	public FilesManager(int ownerId) {
		this.ownerId = ownerId;
		setupDirectory();
	}

	public String getDirName() {
		return System.getProperty("user.dir") + "\\Peer" + this.ownerId + "disk";
	}
	
	public void setupDirectory() {

System.out.println(this.getDirName());
		File dir = new File(this.getDirName());
		// if the directory does not exist, create it
		if (!dir.exists()) {
			try {
				dir.mkdir();
			} catch (SecurityException se) {

			}
		}
	}

	public ArrayList<Chunk> splitToChunks(File file) {

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

					chunkList.add(new Chunk(encryptedId, chunkNo, chunkData));
				}

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return chunkList;
	}

	public void saveChunk(Chunk chunk) {
		
		byte data[] = chunk.getData();
		FileOutputStream out;
		try {

			out = new FileOutputStream(this.getDirName() + "\\" + chunk.getFileId() + chunk.getChunkNo());
			out.write(data);
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void repeatedChunk(Chunk chunk) {
		
	}

	public String encryptFileId(File file) throws NoSuchAlgorithmException {
		String temp = file.getName() + file.lastModified() + ownerId;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(temp.getBytes(StandardCharsets.UTF_8));

		return DatatypeConverter.printHexBinary(hash);
	}
}
