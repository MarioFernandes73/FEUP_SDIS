package messages;

import messages.peerscomunications.*;
import messages.responses.MessageStored;
import peer.ChunkInfo;

import java.util.concurrent.CopyOnWriteArrayList;

public class MessagesRecords {

    private String ownerId;
    private CopyOnWriteArrayList<MessageStored> storedMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageAcceptPeer> acceptPeerMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageRejectPeer> rejectPeerMessages = new CopyOnWriteArrayList<>();

    public MessagesRecords(String ownerId) {
        this.ownerId = ownerId;
    }

    public void addStoredMessage(MessageStored message){
        this.storedMessages.add(message);
    }
    
    public void addAcceptPeerMessage(MessageAcceptPeer message){
        this.acceptPeerMessages.add(message);
    }
    
    public void addRejectPeerMessage(MessageRejectPeer message){
        this.rejectPeerMessages.add(message);
    }

    public void updateChunkInfoStoredMessages(ChunkInfo chunkInfo, String fileId, int chunkNo, String senderId ){
/*
        for (MessageStored storedMessage : storedMessages) {
            if (storedMessage.getFileId().equals(fileId)
                    && storedMessage.getChunkNo() == chunkNo
                    && !chunkInfo.getOwnerIds().contains(senderId)) {
                chunkInfo.getOwnerIds().add(senderId);
            }
        }
*/
    }
}
