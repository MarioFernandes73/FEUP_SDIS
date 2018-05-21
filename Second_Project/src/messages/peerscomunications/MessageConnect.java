package messages.peerscomunications;

import messages.IMessage;
import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessageConnect extends Message implements IMessage {

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
        byte[] res = super.getBaseBytes();
        System.arraycopy(this.getHeader().getBytes(), 0, res, 0, this.getHeader().length());
        return res;
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        if(peer.canAddPeers()){
            peer.addPeer(this.senderId,this.address);
            ArrayList<String> messageArgs = new ArrayList<>();
            messageArgs.add(Constants.MessageType.ACCEPT_CONNECTION.toString());
            messageArgs.add(peer.getId());
            peer.sendMessage(this.senderId,new MessageBuilder().build(messageArgs));
        }

    }
}
