package protocols.protocols;

import messages.MessageBuilder;
import peer.Address;
import peer.ChunkInfo;
import peer.Peer;
import utils.Constants;

import java.util.Map;

public class ChunkDeleteProtocol implements Runnable {

    private Peer peer;
    private ChunkInfo chunkInfo;

    public ChunkDeleteProtocol(Peer peer, ChunkInfo chunkInfo) {
        this.peer = peer;
        this.chunkInfo = chunkInfo;
    }


    @Override
    public void run() {

        try{
            String[] msgArgs = new String[]{
                    Constants.MessageType.SEND_DELETE_CHUNK.toString(),
                    this.peer.getId(),
                    this.chunkInfo.getChunkId()
            };

            for(Map.Entry<String, Address> owner : chunkInfo.getOwners().entrySet()){
                this.peer.sendMessageToAddress(owner.getValue(), MessageBuilder.build(msgArgs));
            }
            Thread.sleep(Constants.RESPONSE_AWAITING_TIME);
            chunkInfo.getOwners().entrySet().removeIf(owner -> this.peer.getRecords().checkForDeletedChunk(owner.getKey(), this.chunkInfo.getChunkId()));


        } catch (Exception e){

        }

    }

    public ChunkInfo getChunkInfo() {
        return chunkInfo;
    }
}
