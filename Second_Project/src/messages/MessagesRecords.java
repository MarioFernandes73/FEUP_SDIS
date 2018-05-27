package messages;

import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import messages.peerscommunications.*;
import messages.responses.MessageChunk;
import messages.responses.MessageReceiveDeleteChunk;
import messages.responses.MessageReceiveFileInfo;
import messages.responses.MessageStored;
import peer.Address;
import peer.ChunkInfo;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessagesRecords {

    private String ownerId;
    private CopyOnWriteArrayList<MessageStored> storedMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageChunk> chunkMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageAcceptPeer> acceptPeerMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageRejectPeer> rejectPeerMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageReceiveFileInfo> receiveFileInfoMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageReceiveDeleteChunk> receiveDeleteChunkMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageRequestPeer> requestPeerMessages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MessageAcceptPeerRequest> acceptPeerRequestMessages = new CopyOnWriteArrayList<>();


    public MessagesRecords(String ownerId) {
        this.ownerId = ownerId;
    }

    public void addStoredMessage(MessageStored message){
        this.storedMessages.add(message);
    }
    
    public void addChunkMessage(MessageChunk message){
        this.chunkMessages.add(message);
    }
    
    public void addAcceptPeerMessage(MessageAcceptPeer message){
        this.acceptPeerMessages.add(message);
    }
    
    public void addRejectPeerMessage(MessageRejectPeer message){
        this.rejectPeerMessages.add(message);
    }
    
    public void addRequestPeerMessage(MessageRequestPeer message) {
    	this.requestPeerMessages.add(message);
    }
    
    public void addAcceptPeerRequestMessage(MessageAcceptPeerRequest message) {
    	this.acceptPeerRequestMessages.add(message);
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
    
    public MessageAcceptPeerRequest getRandomAcceptPeerRequestMessage()
    {
    	if(acceptPeerRequestMessages.size() == 0)
    		return null;
    	
    	Random r = new Random();
    	return acceptPeerRequestMessages.get(r.nextInt(acceptPeerRequestMessages.size()));
    }
    
    public void clearAcceptPeerRequestMessages()
    {
    	acceptPeerRequestMessages.clear();
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
    
    public boolean hasRequestPeerMessage(String peerId)
    {
    	for(int i = requestPeerMessages.size() - 1; i >= 0; i--)
    	{
    		if(requestPeerMessages.get(i).getPeerId().equals(peerId))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean removeRequestPeerMessage(String peerId)
    {
    	for(int i = requestPeerMessages.size() - 1; i >= 0; i--)
    	{
    		if(requestPeerMessages.get(i).getPeerId().equals(peerId))
    		{
    			requestPeerMessages.remove(i);
    			return true;
    		}
    	}
    	return false;
    }

    public HashMap<String, Address> updateChunkInfoGetNextContacts(ChunkInfo chunkInfo){
        HashMap<String, Address> contacts = new HashMap<>();

        for (MessageStored storedMessage : storedMessages) {
            if (storedMessage.getChunkId().equals(chunkInfo.getChunkId())
                    && !chunkInfo.getOwners().containsKey(storedMessage.getSenderId())) {
                chunkInfo.getOwners().put(storedMessage.getSenderId(), storedMessage.getAddress());
                contacts.putAll(storedMessage.getSenderContacts());
                storedMessages.remove(storedMessage);
            }
        }

        return contacts;
    }

    public void addReceiveFileInfoMessage(MessageReceiveFileInfo messageReceiveFileInfo) {
        receiveFileInfoMessages.add(messageReceiveFileInfo);
    }

    public BackedUpFileInfo getFileInfo(String fileId){
        for(MessageReceiveFileInfo msg : this.receiveFileInfoMessages){
            if(msg.getFileInfo().getFileId().equals(fileId)){
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

    public Chunk checkForRestoredChunk(String chunkId) {
        for(MessageChunk messageChunk : this.chunkMessages){
            if(messageChunk.getChunkId().equals(chunkId)){
                return messageChunk.getChunk();
            }
        }
        return null;
    }
}
