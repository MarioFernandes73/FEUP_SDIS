package protocols.initiators;

import filesmanager.BackedUpFileInfo;
import messages.MessageBuilder;
import p.Peer;
import utils.Constants;

public abstract class ProtocolInitiator {

    protected Peer peer;
    protected String clientId;
    protected String fileName;

    ProtocolInitiator(Peer peer, String clientId, String fileName){
        this.peer = peer;
        this.clientId = clientId;
        this.fileName = fileName;
    }

    BackedUpFileInfo findBackedUpFileInfo(String fileId){

        BackedUpFileInfo fileInfo = this.peer.getBackedUpFileInfo(fileId);
        if(fileInfo == null){
            String[] msgArgs = new String[]{
                    Constants.MessageType.SEND_FILE_INFO.toString(),
                    this.peer.getId(),
                    fileId
            };
            try{
                this.peer.sendFloodMessage(MessageBuilder.build(msgArgs));
                Thread.sleep(Constants.RESPONSE_AWAITING_TIME);
            } catch(Exception e){
                e.printStackTrace();
            }

            fileInfo = this.peer.getRecords().getFileInfo(fileId);
        }
        return fileInfo;
    }
}
