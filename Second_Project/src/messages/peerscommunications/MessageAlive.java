package messages.peerscommunications;

import p.Address;
import p.Peer;
import utils.Constants;

import java.util.HashMap;
import java.util.Map.Entry;

import messages.Message;

public class MessageAlive extends Message {

	private HashMap<String, Address> backupForwardingTable = new HashMap<>();
	
	public MessageAlive(String[] args) {
		super(Constants.MessageType.ALIVE, args[1]);
		for(int i = 2; i < args.length; i+=2)
		{
			try {
				backupForwardingTable.put(args[i], new Address(args[i+1]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getHeader() {
		String baseHeader = super.getBaseHeader();
		String header = "";
		for(Entry<String, Address> entry : backupForwardingTable.entrySet())
			header += " " + entry.getKey() + " " + entry.getValue().toString();

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


}
