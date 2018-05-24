package messages.peerscomunications;

import peer.Address;
import peer.Peer;
import utils.Constants;
import java.util.ArrayList;

import messages.Message;

public class MessageAlive extends Message {

	private ArrayList<Address> backupForwardingTable;
	
	protected MessageAlive(String[] args) {
		super(Constants.MessageType.ALIVE, args[0]);
		//missing backupForwardingTable
	}

	@Override
	public String getHeader() {
		return super.getBaseHeader();
		//missing backupForwardingTable
	}

	@Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p = null;
		int count = 0;
		for (Object o : args) {
			if(count == 0)
				p = (Peer) o;
			count++;
		}
		
		p.setAlivePeer(senderId);	
	}
	
	@Override
	public String toString()
	{
		return Constants.MessageType.ALIVE.toString();
	}


}
