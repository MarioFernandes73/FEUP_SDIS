package protocols;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import channels.MulticastChannel;
import utils.Message;
import utils.MessageInterpreter;
import utils.Utils;

public class ChunkBackupProtocol implements Runnable {

	private MulticastChannel channel;
	private Message message;
	private ArrayList<Integer> ownerIds;
	private boolean success = false;

	public ChunkBackupProtocol(MulticastChannel channel, Message message, ArrayList<Integer> ownerIds) {
		this.channel = channel;
		this.message = message;
		this.ownerIds = ownerIds;
	}

	@Override
	public void run() {
		int tries = 0;
		int delay = Utils.FIXED_WAITING_TIME;
		ArrayList<byte[]> receivedBMessagesBytes = new ArrayList<byte[]>();
		while (tries < Utils.MAX_TRIES) {

			try {
				channel.send((message.getHeader() + message.getBody()).getBytes());
				//channel.setSocketTimeout(delay);
				while (true) {
					try {
						// receive a message and add it to an array
						receivedBMessagesBytes.add(channel.receive());
					} catch (SocketTimeoutException e) {
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (byte[] receivedBytes : receivedBMessagesBytes) {
				MessageInterpreter interpreter = new MessageInterpreter(
						new String(receivedBytes, StandardCharsets.UTF_8));
				//interpreter.run();
				if (interpreter.getMessage() != null && interpreter.getMessage().getOperation().equals("STORED")
						&& interpreter.getMessage().getFileId().equals(message.getFileId())) {
					// stored answer to this specific file - update owners
					ownerIds.add(interpreter.getMessage().getSenderId());
				}
			}

			if (ownerIds.size() >= message.getReplicationDeg()) {
				success = true;
				break;
			}

			delay *= 2;
			tries++;
		}

	}

	public ArrayList<Integer> getOwnerIds() {
		return this.ownerIds;
	}

	public boolean getSuccess() {
		return this.success;
	}

}
