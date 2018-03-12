package utils;

import java.io.File;
import java.io.Serializable;

public class FileInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private boolean isBackedUp;
	private int chunksQuantity;
	private int replicationDegree;
	
	public FileInfo(String id, String name, int replicationDegree) {
		this.id = id;
		this.name = name;
		this.isBackedUp = true;
		this.replicationDegree = replicationDegree;
	}
	
	public FileInfo(File file) {
		this.name = file.getName();
		this.isBackedUp = false;
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

	public int getChunksQuantity() {
		return chunksQuantity;
	}
	
	public void setChunksQuantity(int chunksQuantity) {
		this.chunksQuantity = chunksQuantity;
	}
	
	public String getId() {
		return id;
	}
}
