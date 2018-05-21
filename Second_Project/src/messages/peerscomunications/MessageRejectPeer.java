package messages.peerscomunications;

import messages.IMessage;
import messages.Message;
import utils.Constants;

public class MessageRejectPeer extends Message{

    private String peerId;
    
    public MessageRejectPeer(String[] args) {
    	super(Constants.MessageType.REJECT_PEER, args[0]);
    	this.peerId = peerId;
    }

	@Override
	public String getHeader() {
		return super.getBaseHeader() + " " + peerId;
	}

	@Override
	public byte[] getBytes() {
		byte superBytes[] = super.getBaseBytes();
		byte headerBytes[] = getHeader().getBytes();
		
		byte bytes[] = new byte[superBytes.length + headerBytes.length];
		System.arraycopy(superBytes, 0, bytes, 0, superBytes.length);
		System.arraycopy(headerBytes, 0, bytes, superBytes.length, headerBytes.length);
				
		return bytes;
	}

	@Override
	public void handleMessage(Object... args) {
		// TODO Auto-generated method stub
		
	}

}
