package peer;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkInfo implements Serializable {


    private static final long serialVersionUID = 1L;
    private String fileId;
    private int chunkNo;
    private HashMap<String,Address> ownerAddress = new HashMap<>();
    private int desiredReplicationDeg;

    public ChunkInfo(String fileId, int chunkNo, int desiredReplicationDeg) {
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.desiredReplicationDeg = desiredReplicationDeg;
    }

    public ChunkInfo(String chunkInfo) throws UnknownHostException {
        String[] chunkInfoArgs = chunkInfo.split("_");
        this.fileId = chunkInfoArgs[0];
        this.chunkNo = Integer.parseInt(chunkInfoArgs[1]);

        String[] owners = chunkInfoArgs[2].split(",");
        if(owners.length > 0){
            for(String owner : owners){
                String[] ownerInfo = owner.split(">");
                ownerAddress.put(ownerInfo[0], new Address(ownerInfo[1]));
            }
        }

        this.desiredReplicationDeg = Integer.parseInt(chunkInfoArgs[3]);
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

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder(fileId + "_" + chunkNo + "_");
        int i = 0;
        for(Map.Entry<String,Address> owner : this.ownerAddress.entrySet()){
            i++;
            res.append(owner.getKey())
                    .append(">")
                    .append(owner.getValue().toString());
            if(i != this.ownerAddress.size()){
                res.append(",");
            }
        }
        return res.toString() + "_" + desiredReplicationDeg;
    }
}
