package messages.peerscomunications;

import messages.Message;
import peer.Address;

public class MessageAddPeer extends Message {

    private String peerId;
    private Address addressToAdd;

    public MessageAddPeer(){

    }

}
