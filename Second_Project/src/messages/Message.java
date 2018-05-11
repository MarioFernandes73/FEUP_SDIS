package messages;

import peer.Peer;

class Message {

    private byte[] body;

    String getHeader(){
        return "";
    }

    void setBody(byte[] body){
        this.body = body;
    }

    void receivedOperation(Peer peer) {}
}
