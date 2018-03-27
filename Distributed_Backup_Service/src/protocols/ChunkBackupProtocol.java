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
	private boolean success = false;

	public ChunkBackupProtocol(Peer peer, Chunk chunk, int desiredReplicationDegree) {
		this.peer = peer;
		this.chunk = chunk;
		this.desiredReplicationDegree = desiredReplicationDegree;
	}

	@Override
	public void run() {
		int tries = 0;
		int delay = Utils.FIXED_WAITING_TIME;
		this.peer.getStoredMessages().clear();
		while (tries < Utils.MAX_TRIES) {

			Message message = new Message();
			message.prepareMessage("PUTCHUNK", Utils.DEFAULT_VERSION, peer.getId(), chunk.getFileId(),
					chunk.getChunkNo(), this.desiredReplicationDegree, new String(chunk.getData(), StandardCharsets.UTF_8));
			
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

			System.out.println();
			
			for (Message receivedMessages : this.peer.getStoredMessages()) {
				if (receivedMessages.getOperation().equals("STORED")
						&& receivedMessages.getFileId().equals(message.getFileId())) {
					chunk.getOwnerIds().add(receivedMessages.getSenderId());
				}
			}

			if (chunk.getOwnerIds().size() >= message.getReplicationDeg()) {
				success = true;
				break;
			}

			delay *= 2;
			tries++;
		}

	}

	public boolean getSuccess() {
		return this.success;
	}

}
