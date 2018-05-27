package messages.responses;

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

public class MessageChunk extends Message {

    private String chunkId;
    private Address address;

    public MessageChunk(String[] args){
        super(Constants.MessageType.PUT_CHUNK, args[1]);
        this.chunkId = args[2];
        try {
            this.address = new Address(args[3]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHeader() {
    	return super.getBaseHeader() + " " + chunkId + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return Utils.concatenateByteArrays(getHeader().getBytes(), data);
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        peer.getRecords().addChunkMessage(this);
    }

    public String getChunkId(){
        return this.chunkId;
    }

    public Chunk getChunk(){
        return new Chunk(this.chunkId, this.data);
    }

}
