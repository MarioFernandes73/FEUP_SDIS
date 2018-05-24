package messages.peerscomunications;

import messages.IMessage;
import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessageConnect extends Message {

    private Address address;

    public MessageConnect(String[] args) throws UnknownHostException {
        super(Constants.MessageType.CONNECT, args[0]);
        this.address = new Address(args[1], Integer.parseInt(args[2]));
    }


    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.address.toString();
    }

    @Override
    public byte[] getBytes() {
    	return getHeader().getBytes();
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        if(peer.canAddPeers()){
            peer.addPeer(this.senderId,this.address);
            String[] messageArgs = new String[]{Constants.MessageType.ACCEPT_CONNECTION.toString(), peer.getId()};
            peer.sendMessage(this.senderId,new MessageBuilder().build(messageArgs));
        }
        

    }
}
