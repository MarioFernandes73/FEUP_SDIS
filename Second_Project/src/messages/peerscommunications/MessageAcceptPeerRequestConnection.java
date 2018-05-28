package messages.peerscommunications;

import java.io.IOException;
import java.net.UnknownHostException;

import messages.Message;
import messages.MessageBuilder;
import p.Address;
import p.Peer;
import utils.Constants;

public class MessageAcceptPeerRequestConnection extends Message{
	private Address address;

	public MessageAcceptPeerRequestConnection(String[] args) {
		super(Constants.MessageType.ACCEPT_PEER_REQUEST_CONNECTION, args[1]);
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
        try {
            peer.addPeer(this.senderId, this.address);
        
	        if(peer.getNumberConnections() > peer.getPeerLimit())
	        {
	        	peer.changePeerLimit(peer.getPeerLimit() + 1);
	        	String[] message2Args = new String[]{
	                    Constants.MessageType.CHANGE_CONNECTION_LIMIT.toString(),
	                    peer.getId(),
	                    Integer.toString(peer.getPeerLimit())
	            };
	        	peer.sendFloodMessage(MessageBuilder.build(message2Args));
	        }
        } catch (IOException e){
        	e.printStackTrace();
        }
	}
	
}
