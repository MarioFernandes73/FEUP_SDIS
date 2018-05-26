package messages.responses;

import messages.Message;
import peer.Peer;
import utils.Constants;

public class MessageReceiveDeleteChunk extends Message {

    private boolean success = false;
    private String chunkId;

    public MessageReceiveDeleteChunk(String[] args){
        super(Constants.MessageType.RECEIVED_DELETE_CHUNK, args[1]);
        this.chunkId = args[2];
        if(args[3].equals("true")){
            this.success = true;
        }
    }

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        peer.getRecords().addReceiveDeleteChunkMessage(this);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getChunkId(){
        return this.chunkId;
    }
}
