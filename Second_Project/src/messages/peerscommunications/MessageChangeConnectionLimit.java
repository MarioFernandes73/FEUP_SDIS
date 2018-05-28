package messages.peerscommunications;

import java.io.IOException;

import messages.Message;
import messages.MessageBuilder;
import peer.Peer;
import utils.Constants;

public class MessageChangeConnectionLimit extends Message{

	int newLimit;
	
	public MessageChangeConnectionLimit(String[] args) {
		super(Constants.MessageType.CHANGE_CONNECTION_LIMIT, args[1]);
		this.newLimit = Integer.parseInt(args[2]);
	}

	@Override
	public String getHeader() {
		return super.getBaseHeader() + " " + Integer.toString(newLimit) + " \r\n\r\n";
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
        String[] message2Args = new String[]{
                Constants.MessageType.CHANGE_CONNECTION_LIMIT.toString(),
                p.getId(),
                Integer.toString(newLimit)
        };

        try {
            p.sendFloodMessage(MessageBuilder.build(message2Args));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
