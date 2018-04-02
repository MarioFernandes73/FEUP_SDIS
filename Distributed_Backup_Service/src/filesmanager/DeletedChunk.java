package filesmanager;

public class DeletedChunk {
	
	private int owner;
	private ChunkInfo info;

	public DeletedChunk(int owner, ChunkInfo info) {
		this.owner = owner;
		this.info = info;
	}
	
	public int getOwner() {
		return this.owner;
	}
	
	public ChunkInfo getInfo() {
		return this.info;
	}

}
