package messages.peerscomunications;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;
import utils.Constants.MessageType;

public class MessageRequestConnection extends Message{

	Address addressToAdd;
	
	protected MessageRequestConnection(String[] args) {
		super(Constants.MessageType.REQUEST_CONNECTION, args[1]);
		try {
			this.addressToAdd = new Address(args[2], Integer.parseInt(args[3]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getHeader() {
		return super.getBaseHeader() + " " + addressToAdd.toString();
	}

	@Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p = (Peer) args[0];
		try {
			if(p.getNumberConnections() < p.getPeerLimit()){
				p.addPeer(senderId, addressToAdd);
				sendAcceptPeerMessage(p);
			}
			else {
				sendRejectPeerMessage(p);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return Constants.MessageType.REQUEST_CONNECTION.toString();
	}
	
	private void sendAcceptPeerMessage(Peer p) throws IOException
	{
		byte[] responseData;
		String[] responseArgs = new String[3];
		responseArgs[0] = MessageAcceptPeer.class.toString();
		responseArgs[1] = p.getId();
		responseArgs[2] = senderId;
		responseData = MessageBuilder.build(responseArgs).getBytes();
		p.getConnectionAddress(this.senderId).send(responseData);
	}
	
	private void sendRejectPeerMessage(Peer p) throws IOException
	{
		byte[] responseData;
		String[] responseArgs = new String[3];
		responseArgs[0] = MessageRejectPeer.class.toString();
		responseArgs[1] = p.getId();
		responseArgs[2] = senderId;
		responseData = MessageBuilder.build(responseArgs).getBytes();
		//p.getConnectionAddress(this.senderId).send(responseData);
		//TODO use temp socket instead
	}

}
