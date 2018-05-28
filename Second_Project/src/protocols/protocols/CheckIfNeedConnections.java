package protocols.protocols;

import java.io.IOException;
import java.net.SocketException;

import messages.MessageBuilder;
import messages.peerscommunications.MessageAcceptPeerRequest;
import peer.Peer;
import utils.Constants;

public class CheckIfNeedConnections implements Runnable{

	private Peer peer;
	private final long secsToSleep = 10;
	
	public CheckIfNeedConnections(Peer peer)
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
            peer.getRecords().clearRequestPeerMessage();
			if(peer.getNumberConnections() * 1.0 >= (peer.getPeerLimit() / 3.0))
				continue;
			
			String[] messageArgs = new String[] {
					Constants.MessageType.REQUEST_PEER.toString(),
					peer.getId(),
					peer.getId(),
					peer.getIP() + ":" + Integer.toString(peer.getPort())
			};
			
			try {
				peer.sendFloodMessage(MessageBuilder.build(messageArgs));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			MessageAcceptPeerRequest m = peer.getRecords().getRandomAcceptPeerRequestMessage();
			if(m == null)
				continue;
			
			try {
				peer.addPeer(m.getSenderId(), m.getAddress());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String[] messageArgs2 = new String[] {
					Constants.MessageType.ACCEPT_PEER_REQUEST_CONNECTION.toString(),
					peer.getId(),
					peer.getIP() + ":" + Integer.toString(peer.getPort())
			};
			
			try {
				peer.sendMessage(m.getSenderId(), MessageBuilder.build(messageArgs2));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
	}

}
