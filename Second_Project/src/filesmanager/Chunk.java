package filesmanager;

public class Chunk {

    private String chunkId;
    private byte[] data;

    public Chunk(String chunkId, byte[] data) {
        this.chunkId = chunkId;
        this.data = data;
    }

    public String getChunkId() {
        return this.chunkId;
    }

    public byte[] getData() {
        return this.data;
    }

}