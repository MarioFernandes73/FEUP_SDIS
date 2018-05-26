package messages.peerscomunications;

import messages.Message;
import peer.Peer;
import utils.Constants;

public class MessageAcceptPeer extends Message{
	
	private String peerId;
    
    public MessageAcceptPeer(String[] args) {
    	super(Constants.MessageType.ACCEPT_PEER, args[0]);
    	this.peerId = args[1];
    }

	@Override
	public String getHeader() {
		return super.getBaseHeader() + " " + peerId;
	}

	@Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p = (Peer) args[0];
		p.getRecords().addAcceptPeerMessage(this);
	}
	
	@Override
	public String toString()
	{
		return Constants.MessageType.ACCEPT_PEER.toString();
	}
	
	public String getPeerId()
	{
		return peerId;
	}
	
}
