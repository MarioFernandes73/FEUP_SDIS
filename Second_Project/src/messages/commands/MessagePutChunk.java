package messages.commands;

import messages.IMessage;
import messages.Message;
import utils.Constants;

public class MessagePutChunk extends Message implements IMessage {

    private String fileId;
    private int chunkNo;
    private int replicationDegree;
    private int forwardToChildren;
    private int maximumHops;
    private int currentHops;
    private byte[] data;

    public MessagePutChunk(String[] args){
        super(Constants.MessageType.PUT_CHUNK, args[1]);
        System.out.println(args[0]);
        System.out.println(args[1]);
    }

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
