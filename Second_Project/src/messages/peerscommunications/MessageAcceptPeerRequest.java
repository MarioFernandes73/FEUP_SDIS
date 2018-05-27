package messages.peerscommunications;

import java.net.UnknownHostException;

import messages.Message;
import peer.Address;
import peer.Peer;
import utils.Constants;
import utils.Constants.MessageType;

public class MessageAcceptPeerRequest extends Message{

	private Address address;
	
	public MessageAcceptPeerRequest(String[] args) {
		super(Constants.MessageType.ACCEPT_PEER_REQUEST, args[1]);
		try {
			this.address = new Address(args[2]);
		} catch (NumberFormatException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getHeader() {
		return super.getBaseHeader() + " " + this.address.toString() + " \r\n\r\n";
	}

	@Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer peer = (Peer) args[0];
		peer.getRecords().addAcceptPeerRequestMessage(this);	
	}
	
	public String getSenderId()
	{
		return senderId;
	}
	
	public Address getAddress()
	{
		return address;
	}

}
