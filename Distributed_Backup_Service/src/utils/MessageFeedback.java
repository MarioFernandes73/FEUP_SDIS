/**
 * 
 */
package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import channels.MulticastChannel;
import peer.Peer;

public class MessageFeedback implements Runnable {

	private Peer owner = null;
	private byte[] data = null;
	private Message message = null;

	public MessageFeedback(Peer owner, byte[] data) {
		this.data = data;
		this.owner = owner;
	}

	@Override
	public void run() {
		MessageInterpreter interpreter = new MessageInterpreter(new String(data, StandardCharsets.UTF_8));
		this.message = interpreter.parseText();
		if(this.message == null) {
			System.out.println("Message received in the wrong format!");
		}
		switch (message.getOperation()) {
		case "PUTCHUNK":
			this.receivedPutchunkMessage();
			break;
		case "STORED":
			this.receivedStoredMessage();
			break;
		default:
			return;
		}
	}

	private void receivedPutchunkMessage() {
		Message response = new Message();
		response.prepareMessage("STORED", message.getVersion(), owner.getId(), message.getFileId(),
				message.getChunkNo(), -1, null);
		try {
			owner.getMCChannel().send(response.getHeader().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receivedStoredMessage() {
		if(this.owner.getId() == this.message.getSenderId()) {
			//return;
		}
		this.owner.getStoredMessages().add(this.message);
	}
}
