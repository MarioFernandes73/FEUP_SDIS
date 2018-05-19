package messages.peerscomunications;

import messages.IMessage;
import messages.Message;
import peer.Address;

public class MessageConnect extends Message implements IMessage {

    private String senderId;
    private Address address;

    public MessageConnect(){}


    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public void handleMessage(Object... args) {

    }
}
