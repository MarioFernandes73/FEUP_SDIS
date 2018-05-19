package messages.peerscomunications;

import messages.Message;
import peer.Address;
import peer.Peer;
import utils.Constants;

import java.net.UnknownHostException;

public class MessageAcceptConnection extends Message {

    private Address address;

    public MessageAcceptConnection(String[] args) throws UnknownHostException {
        super(Constants.Operation.ACCEPT_CONNECTION, args[0]);
        this.address = new Address(args[1], Integer.parseInt(args[2]));
    }


    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + address.toString();
    }

    @Override
    public byte[] getBytes() {
        byte[] res = super.getBaseBytes();
        System.arraycopy(this.getHeader().getBytes(), 0, res, 0, this.getHeader().length());
        return res;
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        peer.addPeer(this.senderId,this.address);
    }
}
