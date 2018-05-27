package messages.commands;

import filesmanager.Chunk;
import messages.Message;
import messages.MessageBuilder;
import messages.responses.MessageStored;
import peer.Address;
import peer.Peer;
import utils.Constants;
import utils.Utils;

import java.io.IOException;
import java.net.UnknownHostException;

public class MessagePutChunk extends Message {

    private String fileId;
    private int chunkNo;
    private int replicationDegree;
    private byte[] data;
    private Address address;

    public MessagePutChunk(String[] args){
        super(Constants.MessageType.PUT_CHUNK, args[1]);
        this.fileId = args[2];
        this.chunkNo = Integer.parseInt(args[3]);
        this.replicationDegree = Integer.parseInt(args[4]);
        this.data = args[5].getBytes();
        try {
            this.address = new Address(args[6], Integer.parseInt(args[7]));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHeader() {
    	return super.getBaseHeader() + " " + fileId + Integer.toString(chunkNo) + Integer.toString(replicationDegree) + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return Utils.concatenateByteArrays(getHeader().getBytes(), data);
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        
        //peer e quem recebeu putchunk

        //se nao tiver o chunk, guarda o
            //responder com uma stored para quem pediu originalmente a cena e com os filhos

        if(!peer.hasChunk(fileId, chunkNo)){
        	peer.saveChunk(new Chunk((fileId + chunkNo), data));
        	
        	String[] msgArgs = new String[]{
                    Constants.MessageType.STORED_CHUNK.toString(),
                    this.fileId,
                    Integer.toString(this.chunkNo),
                    Integer.toString(replicationDegree-1),
                    peer.getContacts()
            };
            try {
                peer.sendMessageToAddress(this.address,MessageBuilder.build(msgArgs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
