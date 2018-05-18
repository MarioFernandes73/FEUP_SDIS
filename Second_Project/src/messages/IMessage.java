package messages;

public interface IMessage {

    public String getHeader();
    public byte[] getBytes();
    public void handleMessage(Object... args);

}
