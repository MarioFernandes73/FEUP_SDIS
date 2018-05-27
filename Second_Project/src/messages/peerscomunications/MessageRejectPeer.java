package messages.peerscomunications;

import messages.Message;
import peer.Peer;
import utils.Constants;

public class MessageRejectPeer extends Message{

    private String peerId;
    
    public MessageRejectPeer(String[] args) {
    	super(Constants.MessageType.REJECT_PEER, args[1]);
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
		p.getRecords().addRejectPeerMessage(this);
	}
	
	public String getPeerId()
	{
		return peerId;
	}

}
