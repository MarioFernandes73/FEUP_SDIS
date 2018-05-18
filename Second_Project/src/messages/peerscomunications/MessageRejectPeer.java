package messages.peerscomunications;

import messages.IMessage;
import messages.Message;

public class MessageRejectPeer extends Message implements IMessage{

    private String peerId;
    
    public MessageRejectPeer(String peerId) {
    	this.peerId = peerId;
    }

	@Override
	public String getHeader() {
		String header = "";
		
		header += super.getHeader();
		header += " " + peerId;
		
		return header;
	}

	@Override
	public byte[] getBytes() {
		String bytesString = getHeader();
		byte bytes[] = bytesString.getBytes();
				
		return bytes;
	}

	@Override
	public void handleMessage(Object... args) {
		// TODO Auto-generated method stub
		
	}

}
