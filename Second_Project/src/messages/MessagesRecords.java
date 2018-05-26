package messages;

import filesmanager.BackedUpFileInfo;
import messages.peerscomunications.*;
import messages.responses.MessageReceiveDeleteChunk;
import messages.responses.MessageReceiveFileInfo;
import messages.responses.MessageStored;
import peer.ChunkInfo;

import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessagesRecords {

    private String ownerId;
    private CopyOnWriteArrayList<MessageStored> storedMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageAcceptPeer> acceptPeerMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageRejectPeer> rejectPeerMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageReceiveFileInfo> receiveFileInfoMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageReceiveDeleteChunk> receiveDeleteChunkMessages = new CopyOnWriteArrayList<>();

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
    
    public boolean checkAcceptMessage(String peerId)
    {
    	for(int i = acceptPeerMessages.size() - 1; i >= 0; i--)
    	{
    		if(acceptPeerMessages.get(i).getPeerId().equals(peerId))
    		{
    			acceptPeerMessages.remove(i);
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean checkRejectMessage(String peerId)
    {
    	for(int i = rejectPeerMessages.size() - 1; i >= 0; i--)
    	{
    		if(rejectPeerMessages.get(i).getPeerId().equals(peerId))
    		{
    			rejectPeerMessages.remove(i);
    			return true;
    		}
    	}
    	return false;
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

    public void addReceiveFileInfoMessage(MessageReceiveFileInfo messageReceiveFileInfo) {
        receiveFileInfoMessages.add(messageReceiveFileInfo);
    }

    public BackedUpFileInfo getFileInfo(String fileId){
        for(MessageReceiveFileInfo msg : this.receiveFileInfoMessages){
            if(msg.getFileInfo().getId().equals(fileId)){
                return msg.getFileInfo();
            }
        }
        return null;
    }

    public void addReceiveDeleteChunkMessage(MessageReceiveDeleteChunk messageReceiveDeleteChunk) {
        this.receiveDeleteChunkMessages.add(messageReceiveDeleteChunk);
    }

    public boolean checkForDeletedChunk(String peerId, String chunkId){
        for(MessageReceiveDeleteChunk msg : receiveDeleteChunkMessages){
            if(msg.getSenderId().equals(peerId) && msg.getChunkId().equals(chunkId) && msg.isSuccess()){
                return true;
            }
        }
        return false;
    }
}
