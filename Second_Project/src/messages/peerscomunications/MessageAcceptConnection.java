package messages.peerscomunications;

import messages.IMessage;
import messages.Message;
import peer.Address;

import java.net.UnknownHostException;

public class MessageAcceptConnection extends Message implements IMessage {

    private Address address;

    public MessageAcceptConnection(String[] args) throws UnknownHostException {
        this.address = new Address(args[0], Integer.parseInt(args[1]));
    }


    @Override
    public String getHeader() {
        return super.getHeader() + " " + address.toString();
    }

    @Override
    public byte[] getBytes() {
        byte[] res = super.getBytes();
        System.arraycopy(this.getHeader().getBytes(), 0, res, 0, this.getHeader().length());
        return res;
    }

    @Override
    public void handleMessage(Object... args) {

    }
}
