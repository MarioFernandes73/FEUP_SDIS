package messages.peerscommunications;

import java.io.IOException;
import java.net.UnknownHostException;

import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;

public class MessageRequestPeer extends Message{

    private String peerId;
    private Address address;

    public MessageRequestPeer(String[] args){
    	super(Constants.MessageType.REQUEST_PEER, args[1]);
    	this.peerId = args[2];
    	try {
			this.address = new Address(args[3], Integer.parseInt(args[4]));
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
		return super.getBaseHeader() + " " + peerId + " " + address.toString() + " \r\n\r\n";
	}

	@Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer peer = (Peer) args[0];
		if(peer.getRecords().hasRequestPeerMessage(peerId))
			return;
		
		peer.getRecords().addRequestPeerMessage(this);
		try {
			sendAcceptPeerRequestMessage(peer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		peer.getRecords().removeRequestPeerMessage(peerId);
	}
	
	public String getPeerId()
	{
		return peerId;
	}
	
	
	
	private void sendAcceptPeerRequestMessage(Peer p) throws IOException
	{
		String[] acceptArgs = new String[]{
                Constants.MessageType.ACCEPT_PEER_REQUEST.toString(),
                p.getId(),
                p.getIP(),
                Integer.toString(p.getPort())
        };
        p.sendMessageToAddress(address, MessageBuilder.build(acceptArgs));
	}
    
}
