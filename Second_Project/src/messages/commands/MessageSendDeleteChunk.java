package messages.commands;

import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;

import java.io.IOException;
import java.net.UnknownHostException;

public class MessageSendDeleteChunk extends Message {

    private String chunkId;
    private Address address;

    public MessageSendDeleteChunk(String[] args){
        super(Constants.MessageType.SEND_DELETE_CHUNK, args[1]);
        this.chunkId = args[2];
        try {
            this.address = new Address(args[3]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.chunkId + " " + this.address + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return getHeader().getBytes();
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        String success = "false";
        if(peer.deleteChunk(this.chunkId)){
            success = "true";
        }
        String[] msgArgs = new String[]{
                Constants.MessageType.RECEIVE_DELETE_CHUNK.toString(),
                peer.getId(),
                this.chunkId,
                success
        };
        try {
            peer.sendMessageToAddress(this.address,MessageBuilder.build(msgArgs) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
