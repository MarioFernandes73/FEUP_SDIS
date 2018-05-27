package messages.peerscomunications;

import messages.Message;
import peer.Address;
import peer.Peer;
import utils.Constants;

import java.net.SocketException;
import java.net.UnknownHostException;

public class MessageAcceptConnection extends Message {

    private Address address;

    public MessageAcceptConnection(String[] args) throws UnknownHostException {
        super(Constants.MessageType.ACCEPT_CONNECTION, args[1]);
        this.address = new Address(args[2], Integer.parseInt(args[3]));
    }


    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.address.toString() + " \r\n\r\n";
    }

    @Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        try {
            peer.addPeer(this.senderId, this.address);
        } catch (SocketException e){

        }
        System.out.println("OASLDJSAD");
        System.out.println("yey");
    }
}
