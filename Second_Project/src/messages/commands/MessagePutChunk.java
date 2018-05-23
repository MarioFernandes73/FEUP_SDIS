package messages.commands;

import messages.IMessage;
import messages.Message;
import messages.MessageBuilder;
import messages.peerscomunications.MessageAcceptPeer;
import messages.responses.MessageStored;
import peer.Address;
import peer.Peer;
import utils.Constants;

public class MessagePutChunk extends Message implements IMessage {

    private String fileId;
    private int chunkNo;
    private int replicationDegree;
    private byte[] data;

    public MessagePutChunk(String[] args){
        super(Constants.MessageType.PUT_CHUNK, args[0]);
        this.fileId = args[1];
        this.chunkNo = Integer.parseInt(args[2]);
        this.replicationDegree = Integer.parseInt(args[3]);
        this.data = args[4].getBytes();
    }

    @Override
    public String getHeader() {
    	return super.getBaseHeader() + " " + fileId + Integer.toString(chunkNo) + Integer.toString(replicationDegree);
    }

    @Override
    public byte[] getBytes() {
        byte[] res = super.getBaseBytes();
        System.arraycopy(this.getHeader().getBytes(), 0, res, 0, this.getHeader().length());
        return res;
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        
        //peer e quem recebeu putchunk
        
        if(!peer.hasChunk(fileId, chunkNo)){
        	
        	peer.storeChunk(fileId, chunkNo, data);
        	
        	String[] responseArgs = new String[2];
			responseArgs[0] = MessageStored.class.toString();
			responseArgs[1] = peer.getContacts();
			
        	byte[] responseData = MessageBuilder.build(responseArgs).getBytes();

            peer.sendMessage(this.senderId,new MessageBuilder().build(responseArgs));
        }
        
    }
}
