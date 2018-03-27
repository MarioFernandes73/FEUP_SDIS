package utils;

import java.io.File;
import java.io.Serializable;

public class FileInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private boolean isBackedUp = false;
	private int chunksQuantity;
	private boolean replicationDegreeSetted = false;
	private int replicationDegree;
	
	public FileInfo(String id, String name, boolean isBackedUp, int chunksQuantity, int replicationDegree) {
		this.id = id;
		this.name = name;
		this.isBackedUp = isBackedUp;
		if(replicationDegree != -1) {
			replicationDegreeSetted = true;
		}
		this.replicationDegree = replicationDegree;
	}

	public String getName() {
		return name;
	}

	public boolean isBackedUp() {
		return isBackedUp;
	}

	public void setBackedUp(boolean isBackedUp) {
		this.isBackedUp = isBackedUp;
	}
	
	public int getReplicationDegree() {
		return replicationDegree;
	}
	
	public void setReplicationDegree(int repDeg) {
		this.replicationDegreeSetted = true;
		this.replicationDegree = repDeg;
	}

	public int getChunksQuantity() {
		return chunksQuantity;
	}
	
	public void setChunksQuantity(int chunksQuantity) {
		this.chunksQuantity = chunksQuantity;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isReplicationDegreeSetted() {
		return this.replicationDegreeSetted;
	}
}
