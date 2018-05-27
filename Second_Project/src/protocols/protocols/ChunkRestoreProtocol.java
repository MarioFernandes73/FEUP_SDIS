package protocols.protocols;

import filesmanager.Chunk;
import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.ChunkInfo;
import peer.Peer;
import utils.Constants;
import utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChunkRestoreProtocol implements Runnable {

    private Peer peer;
    private ChunkInfo chunkInfo;
    private Chunk chunk;

    public ChunkRestoreProtocol(Peer peer, ChunkInfo chunkInfo) {
        this.peer = peer;
        this.chunkInfo = chunkInfo;
    }

    @Override
    public void run() {
        String[] msgArgs = new String[]{
                Constants.MessageType.GET_CHUNK.toString(),
                this.peer.getId(),
                chunkInfo.getChunkId(),
                this.peer.getIP(),
                Integer.toString(this.peer.getPort())
        };
        Message msg = MessageBuilder.build(msgArgs);

        for(Map.Entry<String, Address> owners : chunkInfo.getOwners().entrySet()){
            try {
                this.peer.sendMessageToAddress(owners.getValue(), msg);
                Thread.sleep(Constants.RESPONSE_AWAITING_TIME);
                Chunk chunk = this.peer.getRecords().checkForRestoredChunk(chunkInfo.getChunkId());
                if(chunk != null){
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public Chunk getChunk() {
        return this.chunk;
    }
}
