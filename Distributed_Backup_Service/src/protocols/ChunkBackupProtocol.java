package protocols;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import communications.Message;
import filesmanager.Chunk;
import filesmanager.ChunkInfo;
import peer.Peer;
import utils.Utils;

public class ChunkBackupProtocol implements Runnable {

	private Peer peer = null;
	private Chunk chunk = null;
	private ChunkInfo chunkInfo = null;

	public ChunkBackupProtocol(Peer peer, Chunk chunk, ChunkInfo chunkInfo) {
		this.peer = peer;
		this.chunk = chunk;
		this.chunkInfo = chunkInfo;
	}

	@Override
	public void run() {
		int tries = 0;
		int delay = Utils.FIXED_WAITING_TIME;
		while (tries < Utils.MAX_TRIES) {
			Message message = new Message();
			try {
				message.prepareMessage("PUTCHUNK", Utils.DEFAULT_VERSION, peer.getId(), chunkInfo.getChunkId(),
						chunkInfo.getChunkNo(), chunkInfo.getDesiredReplicationDeg(),
						new String(chunk.getData(), "ISO_8859_1"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
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
						&& !this.chunkInfo.getOwnerIds().contains(receivedStoredMessages.getSenderId())) {
					this.chunkInfo.getOwnerIds().add(receivedStoredMessages.getSenderId());
				}
			}

			if (this.chunkInfo.getOwnerIds().size() >= chunkInfo.getDesiredReplicationDeg()) {
				break;
			}

			tries++;
		}

	}

	public Chunk getChunk() {
		return this.chunk;
	}

}
