package messages;

import peer.Peer;
import utils.Constants;

public class Message {

    private String senderId;
    private Constants.Operation operation;
    private byte[] body;

    String getHeader(){
        return "";
    }

    void setBody(byte[] body){
        this.body = body;
    }

    void receivedOperation(Peer peer) {}
}
