package messages.peerscommunications;

import filesmanager.BackedUpFileInfo;
import messages.Message;
import p.Peer;
import utils.Constants;

import java.net.UnknownHostException;

public class MessageSendBackedUpFileInfo extends Message {

    private BackedUpFileInfo backedUpFileInfo;

    public MessageSendBackedUpFileInfo(String[] args){
        super(Constants.MessageType.SEND_BACKED_UP_FILE_INFO, args[1]);
        try {
            backedUpFileInfo = new BackedUpFileInfo(args[2]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.backedUpFileInfo.toString() + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return getHeader().getBytes();
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        peer.saveBackedUpFileInfo(this.backedUpFileInfo);
    }
}
