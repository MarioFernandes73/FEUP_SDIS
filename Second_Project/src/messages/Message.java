package messages;

import utils.Constants;

public abstract class Message implements IMessage {

    protected Constants.Operation operation;
    protected String senderId;

    protected Message(Constants.Operation operation, String senderId){
        this.operation = operation;
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
        return operation.toString() + " " + senderId;
    }

}
