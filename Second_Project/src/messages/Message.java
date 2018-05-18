package messages;

import utils.Constants;

public abstract class Message {

    private Constants.Operation operation;
    private String senderId;

    public String getHeader() {
        return operation.toString() + " " + senderId;
    }

    public byte[] getBytes() {
        return this.getHeader().getBytes();
    }

}
