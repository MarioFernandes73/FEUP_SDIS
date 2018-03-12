package utils;

import java.io.File;
import java.io.Serializable;

public class FileInfo implements Serializable {
	
	private String name;
	private long lastModified;
	private boolean isBackedUp;
	
	public FileInfo(File file) {
		this.name = file.getName();
		this.lastModified = file.lastModified();
		this.isBackedUp = false;
	}

	public String getName() {
		return name;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isBackedUp() {
		return isBackedUp;
	}

	public void setBackedUp(boolean isBackedUp) {
		this.isBackedUp = isBackedUp;
	}

}
