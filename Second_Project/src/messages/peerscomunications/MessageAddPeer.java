package messages.peerscomunications;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;

public class MessageAddPeer extends Message{

    private String peerId;
    private Address addressToAdd;

    public MessageAddPeer(String[] args){
    	super(Constants.MessageType.ADD_PEER, args[0]);
    	this.peerId = args[1];
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
		return super.getBaseHeader() + " " + peerId + " " + addressToAdd.toString();
	}

	@Override
	public byte[] getBytes() {				
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p = null;
		
		int count = 0;
		for (Object o : args) {
			if(count == 0)
				p = (Peer) o;
			count++;
		}
		
		byte[] responseData;
		String[] responseArgs = new String[3];
		
		byte[] acceptData;
		String[] acceptArgs = new String[2];
		boolean accepted = false;
		
		if(p.getNumberConnections() < p.getPeerLimit()){
			accepted = true;
			p.addPeer(peerId, addressToAdd);
			responseArgs[0] = MessageAcceptPeer.class.toString();
			responseArgs[1] = p.getId();
			responseArgs[2] = peerId;
			responseData = MessageBuilder.build(responseArgs).getBytes();
		}
		else {
			responseArgs[0] = MessageRejectPeer.class.toString();
			responseArgs[1] = p.getId();
			responseArgs[2] = peerId;
			responseData = MessageBuilder.build(responseArgs).getBytes();
		}
		DatagramSocket responseSocket;
		DatagramSocket acceptSocket;
		try {
			responseSocket = new DatagramSocket();
			Address responseDestinationAddress = p.getConnectionAddress(this.senderId);
		    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, responseDestinationAddress.getInetAddress(), responseDestinationAddress.getPort());
		    responseSocket.send(responsePacket);
		    
		    if(accepted)
		    {
		    	acceptArgs[0] = MessageAcceptConnection.class.toString();
		    	acceptArgs[1] = p.getId();
		    	acceptArgs[2] = p.getIP();
		    	acceptArgs[3] = Integer.toString(p.getPort());
				acceptData = MessageBuilder.build(acceptArgs).getBytes();
		    	
		    	acceptSocket = new DatagramSocket();
				Address acceptDestinationAddress = p.getConnectionAddress(this.senderId);
			    DatagramPacket acceptPacket = new DatagramPacket(acceptData, acceptData.length, acceptDestinationAddress.getInetAddress(), acceptDestinationAddress.getPort());
			    responseSocket.send(acceptPacket);
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
	public String toString()
	{
		return "MESSAGEADDPEER";
	}

}
