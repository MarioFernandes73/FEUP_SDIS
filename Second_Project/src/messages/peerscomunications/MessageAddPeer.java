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
		byte superBytes[] = super.getBaseBytes();
		byte headerBytes[] = getHeader().getBytes();
		
		byte bytes[] = new byte[superBytes.length + headerBytes.length];
		System.arraycopy(superBytes, 0, bytes, 0, superBytes.length);
		System.arraycopy(headerBytes, 0, bytes, superBytes.length, headerBytes.length);
				
		return bytes;
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
		ArrayList<String> responseArgs = new ArrayList<>();
		
		byte[] acceptData;
		ArrayList<String> acceptArgs = new ArrayList<>();
		boolean accepted = false;
		
		if(p.getNumberConnections() < p.getPeerLimit()){
			accepted = true;
			p.addPeer(peerId, addressToAdd);
			responseArgs.add(MessageAcceptPeer.class.toString());
			responseArgs.add(p.getId());
			responseArgs.add(peerId);
			responseData = MessageBuilder.build(responseArgs).getBytes();
		}
		else {
			responseArgs.add(MessageRejectPeer.class.toString());
			responseArgs.add(p.getId());
			responseArgs.add(peerId);
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
		    	acceptArgs.add(MessageAcceptConnection.class.toString());
		    	acceptArgs.add(p.getId());
		    	//acceptArgs.add(p.getIP());
		    	//acceptArgs.add(p.getPort());
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
