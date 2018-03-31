/**
 * 
 */
package communications;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import filesmanager.Chunk;
import filesmanager.ChunkInfo;
import peer.Peer;
import utils.Utils;

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
		case "GETCHUNK":
			this.receivedGetchunkMessage();
			break;
		case "CHUNK":
			this.receivedChunkMessage();
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

		ChunkInfo chunkInfo = new ChunkInfo(message.getFileId(), message.getChunkNo(), message.getReplicationDeg());

		try {
			if (this.owner.getFilesManager().canSaveData(message.getBody().length())
					&& !this.owner.getFilesManager().hasChunk(chunkInfo)) {

				try {
					Thread.sleep(new Random().nextInt(Utils.MAX_RANDOM_DELAY));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (Message storedMessage : this.owner.getStoredMessages()) {
					if (storedMessage.getFileId().equals(message.getFileId())
							&& storedMessage.getChunkNo() == message.getChunkNo()
							&& !chunkInfo.getOwnerIds().contains(message.getSenderId())) {
						chunkInfo.getOwnerIds().add(storedMessage.getSenderId());
					}
				}

				if (chunkInfo.getOwnerIds().size() >= message.getReplicationDeg()) {
					return;
				}

				Message response = new Message();
				response.prepareMessage("STORED", message.getVersion(), owner.getId(), message.getFileId(),
						message.getChunkNo(), -1, null);
				System.out.println("MENSAGEM STORED ENVIADA POR " + owner.getId());
				chunkInfo.getOwnerIds().add(owner.getId());
				owner.getMCChannel().send(response.getHeader().getBytes());
				owner.getFilesManager().getChunksInfo().add(chunkInfo);
				owner.getFilesManager().addChunkToSave(new Chunk(chunkInfo.getChunkId() + chunkInfo.getChunkNo(),
						message.getBody().getBytes("ISO-8859-1")));
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
		this.owner.getFilesManager().updateChunkOwners(message);
		System.out.println("OWNER " + this.owner.getId() + " STORED MESSAGES: " + this.owner.getStoredMessages().size());
	}

	private void receivedGetchunkMessage() {
		if (this.owner.getId() == this.message.getSenderId()) {
			return;
		}
		/*
		 * Chunk chunk = new Chunk(message.getFileId(), message.getChunkNo(),
		 * message.getReplicationDeg(), message.getBody().getBytes());
		 * 
		 * if(this.owner.getFilesManager().hasChunk(chunk)) {
		 * this.owner.getFilesManager().getExistingChunk(message.getFileId()); }
		 * 
		 */
	}

	private void receivedChunkMessage() {
		if (this.owner.getId() == this.message.getSenderId()) {
			return;
		}
		this.owner.getChunkMessages().add(this.message);

	}
}
