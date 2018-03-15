package protocols;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;

import channels.MulticastChannel;
import utils.Message;
import utils.Utils;

public class ChunkBackupProtocol implements Runnable {

	private MulticastChannel channel;
	private Message message;
	private ArrayList<Integer> ownerIds;
	
	public ChunkBackupProtocol(MulticastChannel channel, Message message, ArrayList<Integer> ownerIds) {
		this.channel = channel;
		this.message = message;
		this.ownerIds = ownerIds;
	}
	
	@Override
	public void run() {
		int tries = 0;
		
		while(tries < Utils.MAX_TRIES) {
			int randomDelay = (new Random()).nextInt(Utils.MAX_RANDOM_DELAY + 1);
			
			//channel.send(message.getDatagram());
			try {
				channel.setSocketTimeout(randomDelay);
				
				while(true) {
				/*	try {
						//message = MessageInterpreter.handleMessage(channel.receive());
						 * ownerIds.add(message.getPeerId());
					} catch (SocketTimeoutException e) {
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}*/
				}
				
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
			
			tries++;
		}
	}

}
