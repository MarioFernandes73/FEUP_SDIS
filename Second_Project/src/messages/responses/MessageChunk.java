package messages.responses;

import filesmanager.Chunk;
import messages.Message;
import p.Address;
import p.Peer;
import utils.Constants;
import utils.Utils;

import java.net.UnknownHostException;

public class MessageChunk extends Message {

    private String chunkId;
    private Address address;

    public MessageChunk(String[] args){
        super(Constants.MessageType.CHUNK, args[1]);
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
