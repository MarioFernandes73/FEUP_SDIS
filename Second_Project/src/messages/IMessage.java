package messages;

public interface IMessage {

    String getHeader();
    byte[] getBytes();
    void handleMessage(Object... args);

}
