package messages.commands;

import filesmanager.BackedUpFileInfo;
import messages.Message;
import messages.MessageBuilder;
import p.Peer;
import utils.Constants;

public class MessageSendFileInfo extends Message {

    private String fileId;

    public MessageSendFileInfo(String args[]){
        super(Constants.MessageType.SEND_FILE_INFO, args[1]);
        this.fileId = args[2];

    }

    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.fileId + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
        return getHeader().getBytes();
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];

        try{
            BackedUpFileInfo fileInfo = peer.getBackedUpFileInfo(fileId);
            String fileInfoString = "";
            if(fileInfo != null){
                fileInfoString = fileInfo.toString();
            }
            String[] msgArgs = new String[]{
                    Constants.MessageType.RECEIVE_FILE_INFO.toString(),
                    peer.getId(),
                    fileInfoString
            };
            peer.sendMessage(this.senderId,MessageBuilder.build(msgArgs));
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
