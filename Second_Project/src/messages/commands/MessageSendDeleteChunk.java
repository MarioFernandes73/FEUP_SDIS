package messages.commands;

import messages.Message;
import messages.MessageBuilder;
import peer.Peer;
import utils.Constants;

public class MessageSendDeleteChunk extends Message {

    private String chunkId;

    public MessageSendDeleteChunk(String[] args){
        super(Constants.MessageType.SEND_DELETE_CHUNK, args[1]);
        this.chunkId = args[2];
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
        String success = "false";
        if(peer.deleteChunk(this.chunkId)){
            success = "true";
        }
        String[] msgArgs = new String[]{
                Constants.MessageType.RECEIVED_DELETE_CHUNK.toString(),
                peer.getId(),
                this.chunkId,
                success
        };
        MessageBuilder.build(msgArgs);
    }
}
