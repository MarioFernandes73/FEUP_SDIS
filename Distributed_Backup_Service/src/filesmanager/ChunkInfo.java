package filesmanager;

import java.io.Serializable;
import java.util.ArrayList;

public class ChunkInfo implements Serializable {
	

	private static final long serialVersionUID = 1L;
	private String chunkId;
	private int chunkNo;
	private ArrayList<Integer> ownerIds = new ArrayList<Integer>();
	private int desiredReplicationDeg;
	
	public ChunkInfo(String chunkId, int chunkNo, int desiredReplicationDeg) {
		this.chunkId = chunkId;
		this.chunkNo = chunkNo;
		this.desiredReplicationDeg = desiredReplicationDeg;
	}

	public String getChunkId() {
		return chunkId;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public ArrayList<Integer> getOwnerIds() {
		return ownerIds;
	}

	public int getDesiredReplicationDeg() {
		return desiredReplicationDeg;
	}

}
