package messages.peerscomunications;

import messages.Message;
import peer.Address;
import peer.Peer;
import utils.Constants;
import utils.Utils;

import java.net.UnknownHostException;

public class MessageAcceptConnection extends Message {

    private Address address;

    public MessageAcceptConnection(String[] args) throws UnknownHostException {
        super(Constants.MessageType.ACCEPT_CONNECTION, args[0]);
        this.address = new Address(args[1], Integer.parseInt(args[2]));
    }


    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.address.toString();
    }

    @Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        peer.addPeer(this.senderId,this.address);
    }
}
