package messages.peerscomunications;

import java.io.IOException;
import java.util.Map.Entry;

import messages.Message;
import messages.MessageBuilder;
import peer.Peer;
import peer.TCPSendChannel;
import utils.Constants;

public class MessageChangeConnectionLimit extends Message{

	int newLimit;
	
	protected MessageChangeConnectionLimit(String[] args) {
		super(Constants.MessageType.CHANGE_CONNECTION_LIMIT, args[0]);
		this.newLimit = Integer.parseInt(args[1]);
	}

	@Override
	public String getHeader() {
		return super.getBaseHeader() + " " + Integer.toString(newLimit);
	}

	@Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p = (Peer) args[0];
		
		if(p.getPeerLimit() >= newLimit)
			return;
		
		p.changePeerLimit(newLimit);
		String[] floodArgs = new String[3];
		floodArgs[0] = MessageChangeConnectionLimit.class.toString();
		floodArgs[1] = p.getId();
		floodArgs[2] = Integer.toString(newLimit);
		byte[] floodData = MessageBuilder.build(floodArgs).getBytes();
		for(Entry<String, TCPSendChannel> entry : p.getForwardingTable().entrySet()) {
			try {
				entry.getValue().send(floodData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public String toString() {
		return Constants.MessageType.CHANGE_CONNECTION_LIMIT.toString();
	}

}
