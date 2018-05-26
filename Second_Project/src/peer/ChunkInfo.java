package peer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ChunkInfo implements Serializable {


    private static final long serialVersionUID = 1L;
    private String fileId;
    private int chunkNo;
    private HashMap<String,Address> ownerAddress = new HashMap<>();
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

    public HashMap<String,Address> getOwners() {
        return ownerAddress;
    }

    public int getDesiredReplicationDeg() {
        return desiredReplicationDeg;
    }

    public int getChunkSize() {
        return this.chunkSize;
    }

    public int getPerceivedReplicationDeg() {
        return ownerAddress.size();
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
