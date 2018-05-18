package messages.commands;

import messages.IMessage;
import messages.Message;

public class MessagePutChunk extends Message implements IMessage {

    private String fileId;
    private int chunkNo;
    private int replicationDegree;
    private int forwardToChildren;
    private int maximumHops;
    private int currentHops;
    private byte[] data;

    public MessagePutChunk(){

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
