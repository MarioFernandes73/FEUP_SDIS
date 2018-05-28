package messages.peerscommunications;

import messages.Message;
import p.Peer;
import utils.Constants;

public class MessageAcceptPeer extends Message{
	
	private String peerId;
    
    public MessageAcceptPeer(String[] args) {
    	super(Constants.MessageType.ACCEPT_PEER, args[1]);
    	this.peerId = args[2];
    }

	@Override
	public String getHeader() {
		return super.getBaseHeader() + " " + peerId + " \r\n\r\n";
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
	
	public String getPeerId()
	{
		return peerId;
	}
	
}
