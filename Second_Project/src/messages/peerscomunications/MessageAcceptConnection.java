package messages.peerscomunications;

import messages.Message;
import peer.Address;

import java.net.UnknownHostException;

public class MessageAcceptConnection extends Message {

    private Address address;

    public MessageAcceptConnection(String[] args) throws UnknownHostException {
        this.address = new Address(args[0], Integer.parseInt(args[1]));
    }


}
