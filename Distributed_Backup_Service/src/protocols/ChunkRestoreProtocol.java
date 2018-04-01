package protocols;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.file.Files;

import communications.Message;
import filesmanager.Chunk;
import peer.Peer;
import utils.Utils;

public class ChunkRestoreProtocol implements Runnable {

	private Peer peer = null;
	private String fileId = null;
	private int chunkNo;
	private Chunk chunk = null;

	public ChunkRestoreProtocol(Peer peer, String fileId, int chunkNo) {
		this.peer = peer;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}

	@Override
	public void run() {
		if (this.peer.getFilesManager().hasChunk(this.fileId + this.chunkNo)) {
			try {
				this.chunk = new Chunk(fileId, Files.readAllBytes(
						this.peer.getFilesManager().getExistingChunk(this.fileId + this.chunkNo).toPath()));
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {

			int tries = 0;
			int delay = Utils.FIXED_WAITING_TIME;
			while (tries < Utils.MAX_TRIES) {
				Message message = new Message();
				message.prepareMessage("GETCHUNK", Utils.DEFAULT_VERSION, peer.getId(), this.fileId, this.chunkNo,
						-1, null);
				try {
					peer.getMCChannel().send((message.getHeader()).getBytes("ISO-8859-1"));
					Thread.sleep(delay);
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				for (Message receivedChunkMessages : this.peer.getChunkMessages()) {
					if (receivedChunkMessages.getFileId().equals(message.getFileId())
							&& receivedChunkMessages.getChunkNo() == message.getChunkNo()) {

							this.chunk = new Chunk(this.fileId + this.chunkNo,
									receivedChunkMessages.getBody());
							return;

					}
				}

				tries++;
			}
		}
	}

	public Chunk getChunk() {
		return this.chunk;
	}

}
