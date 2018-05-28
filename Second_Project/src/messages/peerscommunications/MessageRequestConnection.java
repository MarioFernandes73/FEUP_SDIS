package messages.peerscommunications;

import java.io.IOException;

import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;

public class MessageRequestConnection extends Message{

	private Address addressToAdd;
	
	public MessageRequestConnection(String[] args) {
		super(Constants.MessageType.REQUEST_CONNECTION, args[1]);
		try {
			this.addressToAdd = new Address(args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getHeader() {
		return super.getBaseHeader() + " " + addressToAdd.toString() + " \r\n\r\n";
	}

	@Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p = (Peer) args[0];
		if(p.getForwardingTable().containsKey(senderId))
			return;
		
		try {
			if(p.getNumberConnections() >= p.getPeerLimit()){
                //sendRejectPeerMessage(peer);
                p.changePeerLimit(p.getPeerLimit() + 1);
                String[] message2Args = new String[]{
                        Constants.MessageType.CHANGE_CONNECTION_LIMIT.toString(),
                        p.getId(),
                        Integer.toString(p.getPeerLimit())
                };
                p.sendFloodMessage(MessageBuilder.build(message2Args));
			}
            p.addPeer(senderId, addressToAdd);
            sendAcceptPeerMessage(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return Constants.MessageType.REQUEST_CONNECTION.toString();
	}
	
	private void sendAcceptPeerMessage(Peer p) throws IOException
	{
        String[] responseArgs = new String[]{
                Constants.MessageType.ACCEPT_PEER.toString(),
                p.getId(),
                senderId
        };
		p.sendMessage(this.senderId, MessageBuilder.build(responseArgs));
	}
	
	private void sendRejectPeerMessage(Peer p) throws IOException
	{
        String[] responseArgs = new String[]{
                Constants.MessageType.REJECT_PEER.toString(),
                p.getId(),
                senderId
        };
        p.sendMessageToAddress(addressToAdd, MessageBuilder.build(responseArgs));
	}

}
