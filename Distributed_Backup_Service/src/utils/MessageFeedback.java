/**
 * 
 */
package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

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
		if (this.message == null) {
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

	/**
	 * Processes PUTCHUNK message by either disregarding it if it's the same user
	 * who sent it or by checking if the current peer can store the chunk and if so,
	 * saves it to the disk.
	 */

	private void receivedPutchunkMessage() {
		if (message.getSenderId() == owner.getId()) {
			return;
		}
		boolean enhancement = true;

		Chunk chunk = new Chunk(message.getFileId(), message.getChunkNo(), message.getReplicationDeg(),
				message.getBody().getBytes());
		try {
			if (this.owner.getFilesManager().canSaveChunk(chunk)
					&& !this.owner.getFilesManager().hasChunkAlready(chunk)) {

				if (enhancement) {
					try {
						Thread.sleep(new Random().nextInt(Utils.MAX_RANDOM_DELAY));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					for (Message storedMessage : this.owner.getStoredMessages()) {
						if (storedMessage.getFileId().equals(message.getFileId())
								&& storedMessage.getChunkNo() == message.getChunkNo()
								&& !chunk.getOwnerIds().contains(message.getSenderId())) {
							chunk.getOwnerIds().add(message.getSenderId());
						}
					}
					this.owner.getStoredMessages().clear();
				}

				if (chunk.getOwnerIds().size() >= message.getReplicationDeg()) {
					return;
				}

				Message response = new Message();
				response.prepareMessage("STORED", message.getVersion(), owner.getId(), message.getFileId(),
						message.getChunkNo(), -1, null);
				owner.getMCChannel().send(response.getHeader().getBytes());
				owner.getFilesManager().saveChunk(chunk);
				owner.getFilesManager().saveChunksInfo();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes STORED message by either disregarding it if it's the same user who
	 * sent it or by adding it to the designated messages list, as well as checking
	 * if the current peer has the chunk on disk. If so, its replication degree is
	 * updated.
	 */

	private void receivedStoredMessage() {
		if (this.owner.getId() == this.message.getSenderId()) {
			return;
		}
		this.owner.getStoredMessages().add(this.message);
		this.owner.getFilesManager().updateChunk(message);
	}
}
