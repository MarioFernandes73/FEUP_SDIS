package utils;
import java.io.Serializable;
import java.util.ArrayList;

public class Chunk implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String fileId;
	private int chunkNo;
	private byte[] data;
	private ArrayList<Integer> ownerIds = new ArrayList<Integer>();
	private int replicationDegree;
	
	public Chunk(String fileId, int chunkNo, int replicationDegree, byte[] data) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.replicationDegree = replicationDegree;
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public String getFileId() {
		return fileId;
	}

	public ArrayList<Integer> getOwnerIds() {
		return ownerIds;
	}
}
