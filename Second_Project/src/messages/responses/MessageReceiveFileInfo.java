package messages.responses;

import filesmanager.BackedUpFileInfo;
import messages.Message;
import p.Peer;
import utils.Constants;

public class MessageReceiveFileInfo  extends Message {

    private BackedUpFileInfo fileInfo;

    public MessageReceiveFileInfo(String[] args){
        super(Constants.MessageType.RECEIVE_FILE_INFO, args[1]);
        if(args.length != 2){
            int fileInfoArgsLength = args.length - 2;
            String[] fileInfoArgs = new String[fileInfoArgsLength];
            System.arraycopy(args, 2, fileInfoArgs, 0 , fileInfoArgsLength);
            fileInfo = new BackedUpFileInfo(fileInfoArgs);
        }
    }

    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.fileInfo.toString() + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return getHeader().getBytes();
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        peer.getRecords().addReceiveFileInfoMessage(this);
    }

    public BackedUpFileInfo getFileInfo(){
        return this.fileInfo;
    }
}
