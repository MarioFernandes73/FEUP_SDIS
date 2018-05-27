package messages.commands;

import filesmanager.Chunk;
import messages.Message;
import messages.MessageBuilder;
import messages.responses.MessageStored;
import peer.Address;
import peer.Peer;
import peer.TCPSendChannel;
import utils.Constants;
import utils.Utils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map.Entry;

public class MessageGetChunk extends Message {

    private String chunkId;
    private Address address;

    public MessageGetChunk(String[] args){
        super(Constants.MessageType.GET_CHUNK, args[1]);
        this.chunkId = args[2];
        try {
            this.address = new Address(args[3]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHeader() {
    	return super.getBaseHeader() + " " + chunkId + " " + this.address.toString() + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return getHeader().getBytes();
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];

        if(peer.hasChunk(chunkId)){
        	Chunk wantedChunk = peer.getChunk(this.chunkId);
        	
        	String[] msgArgs = new String[]{
                    Constants.MessageType.CHUNK.toString(),
                    peer.getId(),
                    this.chunkId,
                    this.address.toString()
            };
            try {
                Message msg = MessageBuilder.build(msgArgs);
                msg.setData(wantedChunk.getData());
                peer.sendMessageToAddress(this.address, msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
