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
    	return super.getBaseHeader() + " " + fileId + Integer.toString(chunkNo) + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return Utils.concatenateByteArrays(getHeader().getBytes(), data);
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];

        if(peer.hasChunk(fileId + chunkNo)){
        	Chunk wantedChunk = peer.getChunk(fileId, chunkNo);
        	String chunkData = new String(wantedChunk.getData());
        	
        	String[] msgArgs = new String[]{
                    Constants.MessageType.CHUNK.toString(),
                    this.fileId,
                    Integer.toString(this.chunkNo),
                    chunkData,
                    this.address.getIp(),
                    Integer.toString(this.address.getPort())
            };
            try {
                peer.sendMessageToAddress(this.address,MessageBuilder.build(msgArgs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
