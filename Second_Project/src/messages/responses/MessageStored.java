package messages.responses;

import messages.IMessage;
import messages.Message;
import messages.MessageBuilder;
import messages.peerscomunications.MessageAcceptPeer;
import messages.responses.MessageStored;
import peer.Address;
import peer.Peer;
import utils.Constants;
import utils.Utils;

public class MessageStored extends Message {

	private String fileId;
    private int chunkNo;
    private int replicationDegree;
    private String contacts;

    public MessageStored(String[] args){
        super(Constants.MessageType.STORED, args[0]);
        this.fileId = args[1];
        this.chunkNo = Integer.parseInt(args[2]);
        this.replicationDegree = Integer.parseInt(args[3]);
        this.contacts = args[4];
    }

    @Override
    public String getHeader() {
    	return super.getBaseHeader() + " " + contacts;
    }

    @Override
    public byte[] getBytes() {
        return Utils.concatenateByteArrays(getHeader().getBytes(), data);
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        
        //peer e quem recebeu stored
        
        peer.getRecords().addStoredMessage(this);
        
        //if STORED vier com repDegree a 0 nao enviar mais put chunks
        //else
        //enviar put chunk (para um dos temporary contacts) com o repDegree que veio no STORED
        //
        
        /*if(replicationDegree != 0)
        {
        	peer.addTemporaryContacts(contacts);
        	
        	String chosenContact = peer.chooseContact();
        	
        	String[] responseArgs = new String[5];
			responseArgs[0] = MessagePutChunk.class.toString();
			responseArgs[1] = fileId;
			responseArgs[2] = Integer.toString(chunkNo);
			responseArgs[3] = Integer.toString(replicationDegree - 1);
			responseArgs[4] = peer.getBody();
			
        	byte[] responseData = MessageBuilder.build(responseArgs).getBytes();

            peer.sendMessage(chosenContact,new MessageBuilder().build(responseArgs));
        }*/
        
    }
}
