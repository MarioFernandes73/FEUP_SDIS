package utils;
import java.io.Serializable;

public class Chunk {

	private static final long serialVersionUID = 1L;
	
	private String fileId;
	private int chunkNo;
	private byte[] data;
	
	public Chunk(String fileId,int chunkNo, byte[] data) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public String getFileId() {
		return fileId;
	}

}
