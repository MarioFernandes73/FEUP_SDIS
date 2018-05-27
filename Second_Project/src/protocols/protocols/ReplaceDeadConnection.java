package protocols.protocols;

import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import messages.MessageBuilder;
import peer.Address;
import peer.Peer;
import utils.Constants;

public class ReplaceDeadConnection implements Runnable{

	private Peer peer;
	private int nTries = 0;
	private final int maxTries = 3;
	private final long waitingResponseMS = 5000;
	private final long waitingNewTableMS = 30000;
	private boolean accepted;
	private boolean receivedResponse = false;
	
	public ReplaceDeadConnection(Peer p)
	{
		this.peer = p;
	}
	
	@Override
	public void run() {
		while(nTries < maxTries)
		{
			try {	
				ConcurrentHashMap<String, Address> backupTable = peer.getBackupForwardingTable();
				receivedResponse = false;
				for(Entry<String, Address> entry : backupTable.entrySet()) 
		        {
		            if(entry.getKey().equals(peer.getId()))
		            	continue;
					String[] msgArgs = new String[]{
                            Constants.MessageType.REQUEST_CONNECTION.toString(),
                            this.peer.getId(),
                            this.peer.getIP(),
                            Integer.toString(this.peer.getPort())
                    };
					Random random =  new Random();
					Thread.sleep(random.nextInt(400)); //0-400ms delay
                    this.peer.sendMessageToAddress(entry.getValue(), MessageBuilder.build(msgArgs));
					checkReceivedMessage();
					if(accepted)
					{
						peer.addPeer(entry.getKey(), entry.getValue());
						return;
					}
		        }
				nTries++;
				Thread.sleep(waitingNewTableMS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void checkReceivedMessage() throws InterruptedException
	{
		boolean firstIter = true;
		while(!receivedResponse)
		{
			if(!firstIter)
				Thread.sleep(waitingResponseMS);
			firstIter = false;
			if(peer.getRecords().checkAcceptMessage(peer.getId()))
			{
				accepted = true;
				receivedResponse = true;
			}
			if(peer.getRecords().checkRejectMessage(peer.getId()))
			{
				accepted = false;
				receivedResponse = true;
			}
		}
	}

}
