package messages;

import utils.Constants;

public abstract class Message {

    protected Constants.Operation operation;
    protected String senderId;

    public String getHeader() {
        return operation.toString() + " " + senderId;
    }

    public byte[] getBytes() {
        return this.getHeader().getBytes();
    }

}
