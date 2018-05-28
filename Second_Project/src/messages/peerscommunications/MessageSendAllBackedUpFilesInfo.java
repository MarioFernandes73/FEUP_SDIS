package messages.peerscommunications;

import filesmanager.BackedUpFileInfo;
import messages.Message;
import p.Peer;
import utils.Constants;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessageSendAllBackedUpFilesInfo extends Message {

    private ArrayList<BackedUpFileInfo> backedUpFilesInfo = new ArrayList<>();

    public MessageSendAllBackedUpFilesInfo(String[] args){
        super(Constants.MessageType.SEND_ALL_BACKED_UP_FILES_INFO, args[1]);
        try {
            if(args.length > 2 ) {
                String[] filesInfo = args[2].split("~");
                for (String fileInfo : filesInfo) {
                    if (fileInfo.equals("")) {
                        break;
                    }
                    backedUpFilesInfo.add(new BackedUpFileInfo(fileInfo));
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHeader() {
        StringBuilder filesInfo = new StringBuilder();
        for(int i = 0; i < this.backedUpFilesInfo.size(); i++){
            filesInfo.append(this.backedUpFilesInfo.get(i).toString());
            if(i != this.backedUpFilesInfo.size() - 1){
                filesInfo.append("~");
            }
        }
        return super.getBaseHeader() + " " + filesInfo + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return getHeader().getBytes();
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        peer.saveAllBackedUpFilesInfo(this.backedUpFilesInfo);
    }
}
