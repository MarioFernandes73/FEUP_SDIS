package messages.peerscomunications;

import peer.Address;
import peer.Peer;
import utils.Constants;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map.Entry;

import messages.Message;

public class MessageAlive extends Message {

	private HashMap<String, Address> backupForwardingTable = new HashMap<>();
	
	protected MessageAlive(String[] args) {
		super(Constants.MessageType.ALIVE, args[1]);
		for(int i = 2; i < args.length; i+=2)
		{
			String ipPort[] = args[i+1].split(" ");
			try {
				backupForwardingTable.put(args[i], new Address(ipPort[0], Integer.parseInt(ipPort[1])));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getHeader() {
		String baseHeader = super.getBaseHeader();
		String header = "";
		for(Entry<String, Address> entry : backupForwardingTable.entrySet())
			header += " " + entry.getValue().toString();

		return baseHeader + header + " \r\n\r\n";
	}

	@Override
	public byte[] getBytes() {
		return getHeader().getBytes();
	}

	@Override
	public void handleMessage(Object... args) {
		Peer p = (Peer) args[0];
		
		p.setAlivePeer(senderId);
		p.updateBackupTable(backupForwardingTable);
	}
	
	@Override
	public String toString()
	{
		return Constants.MessageType.ALIVE.toString();
	}


}
