package messages.commands;

import messages.IMessage;
import messages.Message;
import messages.MessageBuilder;
import messages.peerscomunications.MessageAcceptPeer;
import messages.responses.MessageStored;
import peer.Address;
import peer.Peer;
import utils.Constants;
import utils.Utils;

import java.io.IOException;

public class MessagePutChunk extends Message {

    private String fileId;
    private int chunkNo;
    private int replicationDegree;
    private byte[] data;

    public MessagePutChunk(String[] args){
        super(Constants.MessageType.PUT_CHUNK, args[1]);
        this.fileId = args[2];
        this.chunkNo = Integer.parseInt(args[3]);
        this.replicationDegree = Integer.parseInt(args[4]);
        this.data = args[5].getBytes();
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
        
        if(!peer.hasChunk(fileId, chunkNo)){
        	
        	//peer.storeChunk(fileId, chunkNo, data);
        	
        	int newRepDegree = replicationDegree-1;
        	
        	String[] responseArgs = new String[5];
			responseArgs[0] = MessageStored.class.toString();
			responseArgs[1] = fileId;
			responseArgs[2] = Integer.toString(chunkNo);
			responseArgs[3] = Integer.toString(newRepDegree);
			responseArgs[4] = peer.getContacts();
			
        	byte[] responseData = MessageBuilder.build(responseArgs).getBytes();

            try {
                peer.sendMessage(this.senderId,new MessageBuilder().build(responseArgs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
