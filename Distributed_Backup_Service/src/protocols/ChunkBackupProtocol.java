package protocols;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import channels.MulticastChannel;
import peer.Peer;
import utils.Chunk;
import utils.Message;
import utils.MessageInterpreter;
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

			for (Message receivedMessages : this.peer.getStoredMessages()) {
				if (receivedMessages.getOperation().equals("STORED")
						&& receivedMessages.getFileId().equals(message.getFileId())
						&& !this.chunk.getOwnerIds().contains(message.getSenderId())) {
					this.chunk.getOwnerIds().add(receivedMessages.getSenderId());
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
