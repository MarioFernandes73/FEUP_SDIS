package messages.peerscomunications;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import messages.IMessage;
import messages.Message;
import peer.Address;
import peer.Peer;

public class MessageAddPeer extends Message{

    private String peerId;
    private Address addressToAdd;

    public MessageAddPeer(String peerId, Address addressToAdd){
    	this.peerId = peerId;
    	this.addressToAdd = addressToAdd;
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
		
		byte[] data;
		
		if(p.getNumberConnections() < p.getPeerLimit()){
			p.addPeer(peerId, addressToAdd);
			data = new MessageAcceptPeer(peerId).getBytes();
		}
		else
			data = new MessageRejectPeer(peerId).getBytes();
		
		DatagramSocket sendSocket = new DatagramSocket();

        Address DestinationAddress = p.getConnectionAddress(this.senderId);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, DestinationAddress.getInetAddress(), DestinationAddress.getPort());
        sendSocket.send(sendPacket);		
	}
    

}
