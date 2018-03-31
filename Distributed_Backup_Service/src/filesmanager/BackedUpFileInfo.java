package filesmanager;

import java.io.Serializable;
import java.util.ArrayList;

public class BackedUpFileInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private long lastModifiedDate;
	private boolean isBackedUp = false;
	private ArrayList<ChunkInfo> backedUpChunks = new ArrayList<ChunkInfo>();
	
	public BackedUpFileInfo(String id, String name, long lastModifiedDate, boolean isBackedUp) {
		this.id = id;
		this.name = name;
		this.lastModifiedDate = lastModifiedDate;
		this.isBackedUp = isBackedUp;
	}

	public String getName() {
		return name;
	}
	
	public long getLastModifiedDate() {
		return this.lastModifiedDate;
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
	
	public ArrayList<ChunkInfo> getBackedUpChunks() {
		return this.backedUpChunks;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof BackedUpFileInfo) {
			if(((BackedUpFileInfo)object).getName().equals(this.getName())) {
				return true;
			}
		}
		return false;
	}
}
