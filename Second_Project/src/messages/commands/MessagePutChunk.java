package messages.commands;

import filesmanager.Chunk;
import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.ChunkInfo;
import peer.Peer;
import utils.Constants;
import utils.Utils;

import java.io.IOException;
import java.net.UnknownHostException;

public class MessagePutChunk extends Message {

    private String chunkId;
    private Address address;
    private int replicationDeg;

    public MessagePutChunk(String[] args){
        super(Constants.MessageType.PUT_CHUNK, args[1]);
        this.chunkId = args[2];
        try {
            this.address = new Address(args[3]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.replicationDeg = Integer.parseInt(args[4]);
    }

    @Override
    public String getHeader() {
    	return super.getBaseHeader() + " " + chunkId + " " + address.toString() + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return Utils.concatenateByteArrays(getHeader().getBytes(), data);
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];

        if(!peer.hasChunk(chunkId)){
        	peer.saveChunk(new Chunk(chunkId, data));
        	peer.addChunkInfo(new ChunkInfo(chunkId.substring(0,64),Integer.parseInt(chunkId.substring(64)), this.replicationDeg));
        	String[] msgArgs = new String[]{
                    Constants.MessageType.STORED_CHUNK.toString(),
                    peer.getId(),
                    this.chunkId,
                    peer.getContactsExcept(this.senderId),
                    peer.getIP() + ":" + Integer.toString(peer.getPort())
            };
            try {
                peer.sendMessageToAddress(this.address,MessageBuilder.build(msgArgs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
