package messages.responses;

import messages.Message;
import peer.Peer;
import utils.Constants;

public class MessageReceiveDeleteChunk extends Message {

    private String chunkId;
    private boolean success = false;

    public MessageReceiveDeleteChunk(String[] args){
        super(Constants.MessageType.RECEIVE_DELETE_CHUNK, args[1]);
        this.chunkId = args[2];
        if(args[3].equals("true")){
            this.success = true;
        }
    }

    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.chunkId + " " + this.success + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
            return getHeader().getBytes();
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
