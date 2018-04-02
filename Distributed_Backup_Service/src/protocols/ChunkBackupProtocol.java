package protocols;

import java.io.ByteArrayOutputStream;
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
	private ChunkInfo chunkInfo = null;
	private byte[] chunkData = null;
	private boolean enhancement = false;

	public ChunkBackupProtocol(Peer peer, ChunkInfo chunkInfo, byte[] chunkData, boolean enhancement) {
		this.peer = peer;
		this.chunkData = chunkData;
		this.chunkInfo = chunkInfo;
		this.enhancement = enhancement;
	}

	@Override
	public void run() {
		int tries = 0;
		int delay = Utils.FIXED_WAITING_TIME;
		while (tries < Utils.MAX_TRIES) {
			Message message = new Message();
			String version = Utils.DEFAULT_VERSION;
			if(enhancement) {
				version = "2.0";
			}
				message.prepareMessage("PUTCHUNK", version, peer.getId(), chunkInfo.getFileId(),
						chunkInfo.getChunkNo(), chunkInfo.getDesiredReplicationDeg(),
						chunkData);
			try {
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try 
				{
					baos.write(message.getHeader().getBytes());
					baos.write(message.getBody());
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				byte[] payload = baos.toByteArray();
				peer.getMDBChannel().send(payload);
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

}
