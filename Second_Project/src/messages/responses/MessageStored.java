package messages.responses;

import messages.Message;
import peer.Address;
import peer.Peer;
import utils.Constants;
import utils.Utils;

import java.net.UnknownHostException;
import java.util.HashMap;


public class MessageStored extends Message {

	private String chunkId;
	private String contacts;
	private HashMap<String, Address> senderContacts;
    private Address address;

    public MessageStored(String[] args){
        super(Constants.MessageType.STORED_CHUNK, args[1]);
        this.chunkId = args[2];
        this.contacts = args[3];
        this.senderContacts = Utils.createContacts(args[3]);
        try {
            this.address = new Address(args[4]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHeader() {
    	return super.getBaseHeader() + " " + this.chunkId + " " + this.contacts + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return Utils.concatenateByteArrays(getHeader().getBytes(), data);
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        peer.getRecords().addStoredMessage(this);
    }

    public String getChunkId(){
        return this.chunkId;
    }

    public HashMap<String, Address> getSenderContacts(){
        return this.senderContacts;
    }

    public Address getAddress(){
        return this.address;
    }
}
