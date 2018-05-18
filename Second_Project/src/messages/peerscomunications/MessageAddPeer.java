package messages.peerscomunications;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import messages.IMessage;
import messages.Message;
import peer.Address;
import peer.Peer;

public class MessageAddPeer extends Message implements IMessage{

    private String peerId;
    private Address addressToAdd;

    public MessageAddPeer(){

    }

	@Override
	public String getHeader() {
		String header = "";
		
		header += super.getHeader();
		header += " " + peerId + " " + addressToAdd.toString();
		
		return header;
	}

	@Override
	public byte[] getBytes() {
		String bytesString = getHeader();
		byte bytes[] = bytesString.getBytes();
				
		return bytes;
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p;
		
		int count = 0;
		for (Object o : args) {
			if(count == 0)
				p = (Peer) o;
			count++;
		}
		
		byte[] data;
		
		if(p.getNumberConnections() < p.getMaxConnections){
			p.addPeer(peerId, AddressToAdd);
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
