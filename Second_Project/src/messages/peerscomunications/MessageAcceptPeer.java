package messages.peerscomunications;

import messages.Message;
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString()
	{
		return "MESSAGEACCEPTPEER";
	}
	
}
