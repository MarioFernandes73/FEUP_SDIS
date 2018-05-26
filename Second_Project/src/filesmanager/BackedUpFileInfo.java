package filesmanager;

import peer.ChunkInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class BackedUpFileInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private long lastModifiedDate;
    private boolean isBackedUp = false;
    private boolean toDelete = false;
    private ArrayList<ChunkInfo> backedUpChunks = new ArrayList<>();

    public BackedUpFileInfo(String id, String name, long lastModifiedDate, boolean isBackedUp) {
        this.id = id;
        this.name = name;
        this.lastModifiedDate = lastModifiedDate;
        this.isBackedUp = isBackedUp;
    }

    public BackedUpFileInfo(String[] fileInfoArgs) {
    }

    public String getName() {
        return name;
    }

    public long getLastModifiedDate() {
        return lastModifiedDate;
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

    public int getDesiredReplicationDeg() {
        if(backedUpChunks.size() <= 0)
            return -1;
        else
            return backedUpChunks.get(0).getDesiredReplicationDeg();
    }

    public ArrayList<ChunkInfo> getBackedUpChunks() {
        return this.backedUpChunks;
    }

    public String getChunksInfo() {
        String info = "";
        for(ChunkInfo chunk: backedUpChunks) {
            info += "\n \tId: " + chunk.getChunkId();
            info += "\n \tPerceived replication degree: " + chunk.getPerceivedReplicationDeg() + "\n";
        }
        return info;
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

    public void clearChunks() {
        this.backedUpChunks = new ArrayList<ChunkInfo>();
    }

    public boolean getToDelete() {
        return this.toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }
}