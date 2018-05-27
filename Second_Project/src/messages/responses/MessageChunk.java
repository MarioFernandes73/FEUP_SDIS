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

    private String fileId;
    private int chunkNo;
    private byte[] data;
    private Address address;

    public MessageChunk(String[] args){
        super(Constants.MessageType.PUT_CHUNK, args[1]);
        this.fileId = args[2];
        this.chunkNo = Integer.parseInt(args[3]);
        this.data = args[4].getBytes();
        try {
            this.address = new Address(args[5], Integer.parseInt(args[6]));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHeader() {
    	return super.getBaseHeader() + " " + fileId + Integer.toString(chunkNo) + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return Utils.concatenateByteArrays(getHeader().getBytes(), data);
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        
        //peer e quem recebeu chunk
        
        peer.getRecords().addChunkMessage(this);
        
    }
}
