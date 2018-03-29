package protocols;

import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import peer.Peer;
import utils.Chunk;
import utils.Message;
import utils.Utils;

public class ChunkBackupProtocol implements Runnable {

	private Peer peer = null;
	private Chunk chunk = null;
	private int desiredReplicationDegree;

	public ChunkBackupProtocol(Peer peer, Chunk chunk, int desiredReplicationDegree) {
		this.peer = peer;
		this.chunk = chunk;
		this.desiredReplicationDegree = desiredReplicationDegree;
	}

	@Override
	public void run() {
		int tries = 0;
		int delay = Utils.FIXED_WAITING_TIME;
		while (tries < Utils.MAX_TRIES) {

			Message message = new Message();
			message.prepareMessage("PUTCHUNK", Utils.DEFAULT_VERSION, peer.getId(), chunk.getFileId(),
					chunk.getChunkNo(), this.desiredReplicationDegree,
					new String(chunk.getData(), StandardCharsets.UTF_8));
			try {
				peer.getMDBChannel().send((message.getHeader() + message.getBody()).getBytes());
				Thread.sleep(delay);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Message receivedStoredMessages : this.peer.getStoredMessages()) {
				if (receivedStoredMessages.getFileId().equals(message.getFileId())
						&& receivedStoredMessages.getChunkNo() == message.getChunkNo()
						&& !this.chunk.getOwnerIds().contains(receivedStoredMessages.getSenderId())) {
					this.chunk.getOwnerIds().add(receivedStoredMessages.getSenderId());
				}
			}

			if (this.chunk.getOwnerIds().size() >= this.desiredReplicationDegree) {
				break;
			}

			tries++;
		}

	}

	public Chunk getChunk() {
		return this.chunk;
	}

}
