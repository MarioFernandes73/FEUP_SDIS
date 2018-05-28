package filesmanager;

import p.ChunkInfo;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class BackedUpFileInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fileId;
    private String fileName;
    private ArrayList<ChunkInfo> backedUpChunks = new ArrayList<>();

    public BackedUpFileInfo(String id, String name, ArrayList<ChunkInfo> chunkInfos) {
        this.fileId = id;
        this.fileName = name;
        this.backedUpChunks = chunkInfos;
    }

    public BackedUpFileInfo(String fileInfo) throws UnknownHostException {
        String[] fileInfoArgs = fileInfo.split("-");
        this.fileId = fileInfoArgs[0];
        this.fileName = fileInfoArgs[1];
        for(int i = 2; i < fileInfoArgs.length; i++){
            backedUpChunks.add(new ChunkInfo(fileInfoArgs[i]));
        }
    }

    public BackedUpFileInfo(String[] fileInfoArgs) {
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileId() {
        return fileId;
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
        StringBuilder info = new StringBuilder();
        for(ChunkInfo chunk: backedUpChunks) {
            info.append("\n \tId: ").append(chunk.getChunkId());
            info.append("\n \tPerceived replication degree: ").append(chunk.getPerceivedReplicationDeg()).append("\n");
        }
        return info.toString();
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof BackedUpFileInfo) {
            return ((BackedUpFileInfo) object).getFileName().equals(this.getFileName());
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder(fileId + "-" + fileName);
        for(ChunkInfo chunkInfo : this.backedUpChunks){
            res.append("-").append(chunkInfo.toString());
        }
        return res.toString();
    }
}