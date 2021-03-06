package filesmanager;

import java.io.Serializable;
import java.util.ArrayList;

public class ChunkInfo implements Serializable {
	

	private static final long serialVersionUID = 1L;
	private String fileId;
	private int chunkNo;
	private ArrayList<Integer> ownerIds = new ArrayList<Integer>();
	private int desiredReplicationDeg;
	private int chunkSize;
	
	public ChunkInfo(String fileId, int chunkNo, int desiredReplicationDeg, int chunkSize) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.desiredReplicationDeg = desiredReplicationDeg;
		this.chunkSize = chunkSize;
	}

	public String getFileId() {
		return fileId;
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
	
	public int getChunkSize() {
		return this.chunkSize;
	}

	public int getPerceivedReplicationDeg() {
		return ownerIds.size();
	}
	
	public String getChunkId() {
		return fileId + chunkNo;
	}
	
	public boolean belongsToFile(String fileId) {
		return this.fileId.equals(fileId);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof ChunkInfo) {
			return (((ChunkInfo) object)).getChunkId().equals(this.getChunkId());
		}
		return false;
	}
}
