package messages.peerscommunications;

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
			this.addressToAdd = new Address(args[3]);
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
		
		try {
			if(p.getNumberConnections() < p.getPeerLimit()){
				p.addPeer(peerId, addressToAdd);
				sendAcceptPeerMessage(p);
				sendAcceptConnectionMessage(p);
				String[] msgArgs = new String[]{
						Constants.MessageType.SEND_ALL_BACKED_UP_FILES_INFO.toString(),
						p.getId(),
						p.getBackedUpFilesInfo()
				};
				p.sendMessageToAddress(this.addressToAdd,MessageBuilder.build(msgArgs));
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
	
	private void sendAcceptPeerMessage(Peer p) throws IOException
	{
		
		String[] responseArgs = new String[]{
                Constants.MessageType.ACCEPT_PEER.toString(),
                p.getId(),
                peerId
	};
		p.sendMessage(this.senderId, MessageBuilder.build(responseArgs));
	}
	
	private void sendRejectPeerMessage(Peer p) throws IOException
	{
		String[] responseArgs = new String[]{
                Constants.MessageType.REJECT_PEER.toString(),
                p.getId(),
                peerId
        };
        p.sendMessage(this.senderId, MessageBuilder.build(responseArgs));
	}
	
	private void sendAcceptConnectionMessage(Peer p) throws IOException
	{
		String[] acceptArgs = new String[]{
                Constants.MessageType.ACCEPT_CONNECTION.toString(),
                p.getId(),
                p.getIP() + ":" + Integer.toString(p.getPort())
        };
        p.sendMessage(peerId, MessageBuilder.build(acceptArgs));
	}
    

}
