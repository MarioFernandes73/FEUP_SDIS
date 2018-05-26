package messages.peerscomunications;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;

public class MessageAddPeer extends Message{

    private String peerId;
    private Address addressToAdd;

    public MessageAddPeer(String[] args){
    	super(Constants.MessageType.ADD_PEER, args[1]);
    	this.peerId = args[2];
    	try {
			this.addressToAdd = new Address(args[3], Integer.parseInt(args[4]));
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
		return super.getBaseHeader() + " " + peerId + " " + addressToAdd.toString() + " \r\n\r\n";
	}

	@Override
	public byte[] getBytes() {				
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p = (Peer) args[0];
		boolean accepted = false;
		
		try {
			if(p.getNumberConnections() < p.getPeerLimit()){
				accepted = true;
				p.addPeer(peerId, addressToAdd);
				sendAcceptPeerMessage(p);
			}
			else {
				sendRejectPeerMessage(p);
			}
			
		    if(accepted)
		    {
		    	sendAcceptConnectionMessage(p);
		    }	
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendAcceptPeerMessage(Peer p) throws IOException
	{
		
		String[] responseArgs = new String[3];
		responseArgs[0] = MessageAcceptPeer.class.toString();
		responseArgs[1] = p.getId();
		responseArgs[2] = peerId;
		byte[] responseData = MessageBuilder.build(responseArgs).getBytes();
		p.getConnectionAddress(this.senderId).send(responseData);
	}
	
	private void sendRejectPeerMessage(Peer p) throws IOException
	{
		String[] responseArgs = new String[3];
		responseArgs[0] = MessageRejectPeer.class.toString();
		responseArgs[1] = p.getId();
		responseArgs[2] = peerId;
		byte[] responseData = MessageBuilder.build(responseArgs).getBytes();
		p.getConnectionAddress(this.senderId).send(responseData);
	}
	
	private void sendAcceptConnectionMessage(Peer p) throws IOException
	{
		String[] acceptArgs = new String[4];
		acceptArgs[0] = MessageAcceptConnection.class.toString();
    	acceptArgs[1] = p.getId();
    	acceptArgs[2] = p.getIP();
    	acceptArgs[3] = Integer.toString(p.getPort());
		byte[] acceptData = MessageBuilder.build(acceptArgs).getBytes();
		p.getConnectionAddress(peerId).send(acceptData);
	}
    
	@Override
	public String toString()
	{
		return Constants.MessageType.ADD_PEER.toString();
	}

}
