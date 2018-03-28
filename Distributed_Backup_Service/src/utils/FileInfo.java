package utils;

import java.io.Serializable;
import java.util.ArrayList;

public class FileInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private boolean isBackedUp = false;
	private ArrayList<Chunk> backedUpChunks = new ArrayList<Chunk>();
	
	public FileInfo(String id, String name, boolean isBackedUp) {
		this.id = id;
		this.name = name;
		this.isBackedUp = isBackedUp;
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
	
	public String getId() {
		return id;
	}
	
	public ArrayList<Chunk> getBackedUpChunks() {
		return this.backedUpChunks;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof FileInfo) {
			if(((FileInfo)object).getName().equals(this.getName())) {
				return true;
			}
		}
		return false;
	}
}
