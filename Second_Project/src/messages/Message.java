package messages;

import utils.Constants;

public abstract class Message implements IMessage {

    protected Constants.MessageType messageType;
    protected String senderId;
    protected byte[] data;

    protected Message(Constants.MessageType messageType, String senderId){
        this.messageType = messageType;
        this.senderId = senderId;
    }

    @Override
    public abstract String getHeader();

    @Override
    public abstract byte[] getBytes();

    @Override
    public abstract void handleMessage(Object... args);

    protected byte[] getBaseBytes(){
        return this.getHeader().getBytes();
    }

    protected String getBaseHeader(){
        return messageType.toString() + " " + senderId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
