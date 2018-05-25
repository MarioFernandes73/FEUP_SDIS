package messages.peerscomunications;

import messages.Message;
import peer.Peer;
import utils.Constants;

public class MessageRejectPeer extends Message{

    private String peerId;
    
    public MessageRejectPeer(String[] args) {
    	super(Constants.MessageType.REJECT_PEER, args[0]);
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
		p.getRecords().addRejectPeerMessage(this);
	}
	
	@Override
	public String toString()
	{
		return Constants.MessageType.REJECT_PEER.toString();
	}

}
