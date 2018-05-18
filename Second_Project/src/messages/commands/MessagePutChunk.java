package messages.commands;

import messages.Message;

public class MessagePutChunk extends Message {

    private String senderId;
    private String fileId;
    private int chunkNo;
    private int replicationDegree;
    private int forwardToChildren;
    private int maximumHops;
    private int currentHops;
    private byte[] data;

    public MessagePutChunk(){

    }

}
