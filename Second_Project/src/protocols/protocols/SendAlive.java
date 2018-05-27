package protocols.protocols;

import java.io.IOException;
import java.util.Map.Entry;

import messages.MessageBuilder;
import peer.Peer;
import peer.TCPSendChannel;
import utils.Constants;

public class SendAlive implements Runnable{
	
	private Peer peer;
	private long secsToSleep = 5;
	
	public SendAlive(Peer peer)
	{
		this.peer = peer;
	}
	
	@Override
	public void run() {
		while(true)
		{
			try {
				Thread.sleep(secsToSleep * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(peer.getNumberConnections() == 0)
				continue;
			
			String[] messageArgs = new String[2 + peer.getNumberConnections() * 3];
			messageArgs[0] = Constants.MessageType.ALIVE.toString();
			messageArgs[1] = peer.getId();
			int i = 2;
			for(Entry<String, TCPSendChannel> entry : peer.getForwardingTable().entrySet())
			{
				messageArgs[i] = entry.getKey();
				messageArgs[i+1] = entry.getValue().getAddress().getIp();
				messageArgs[i+2] = Integer.toString(entry.getValue().getAddress().getPort());
				i+=3;
			}
			
			try {
				peer.sendFloodMessage(MessageBuilder.build(messageArgs));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

}
