package messages.peerscomunications;

import messages.Message;
import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import peer.TCPSendChannel;
import utils.Constants;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class MessageConnect extends Message {

    private Address address;
    private final long waitingResponseMS = 1000;

    public MessageConnect(String[] args) throws UnknownHostException {
        super(Constants.MessageType.CONNECT, args[1]);
        this.address = new Address(args[2], Integer.parseInt(args[3]));
    }


    @Override
    public String getHeader() {
        return super.getBaseHeader() + " " + this.address.toString() + " \r\n\r\n";
    }

    @Override
    public byte[] getBytes() {
    	return getHeader().getBytes();
    }

    @Override
    public void handleMessage(Object... args) {
        Peer peer = (Peer) args[0];
        try{
            if(peer.canAddPeers()){
                peer.addPeer(this.senderId,this.address);
                String[] messageArgs = new String[]{
                        Constants.MessageType.ACCEPT_CONNECTION.toString(),
                        peer.getId(),
                        peer.getIP(),
                        Integer.toString(peer.getPort())
                };
                peer.sendMessage(this.senderId,new MessageBuilder().build(messageArgs));
            }
            else if(!askConnections(peer))
            {
            	addPeerNIncreaseLimit(peer);
            }
        } catch (SocketException e){

        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(peer.getPeerLimit());
    }
    
    
	public boolean askConnections(Peer peer) throws InterruptedException, IOException {
    	
    	ConcurrentHashMap<String, TCPSendChannel> forwardingTable = peer.getForwardingTable();
    	String[] messageArgs = new String[]{
                Constants.MessageType.ADD_PEER.toString(),
                peer.getId(),
                this.senderId,
                address.getIp(),
                Integer.toString(address.getPort())
		};
		byte[] messageData = MessageBuilder.build(messageArgs).getBytes();
		for(Entry<String, TCPSendChannel> entry : forwardingTable.entrySet()) 
		{
			entry.getValue().send(messageData);
			if(checkReceivedMessage(peer))
			{
				return true;
			}
        }
		
		return false;
    }
    
    public boolean checkReceivedMessage(Peer peer) throws InterruptedException
	{
		boolean firstIter = true;
		boolean receivedResponse = false;
		while(!receivedResponse)
		{
			if(!firstIter)
				Thread.sleep(waitingResponseMS);
			firstIter = false;
			if(peer.getRecords().checkAcceptMessage(senderId))
			{
				return true;
			}
			if(peer.getRecords().checkRejectMessage(senderId))
			{
				return false;
			}
		}
		return false;
	}
    
    private void addPeerNIncreaseLimit(Peer peer) throws IOException {
    	peer.addPeer(this.senderId,this.address);
        String[] messageArgs = new String[]{
                Constants.MessageType.ACCEPT_CONNECTION.toString(),
                peer.getId(),
                peer.getIP(),
                Integer.toString(peer.getPort())
        };
        peer.sendMessage(this.senderId,new MessageBuilder().build(messageArgs));
    	
    	peer.changePeerLimit(peer.getPeerLimit() + 1);
    	ConcurrentHashMap<String, TCPSendChannel> forwardingTable = peer.getForwardingTable();
    	String[] message2Args = new String[]{
                Constants.MessageType.CHANGE_CONNECTION_LIMIT.toString(),
                peer.getId(),
                Integer.toString(peer.getPeerLimit())
        };
    	peer.sendFloodMessage(MessageBuilder.build(message2Args));
	}
    
}
