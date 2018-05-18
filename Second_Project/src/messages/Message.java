package messages;

import utils.Constants;

public class Message implements IMessage {

    protected String senderId;
    protected Constants.Operation operation;

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
