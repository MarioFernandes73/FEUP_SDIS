package protocols;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import communications.Message;
import filesmanager.ChunkInfo;
import peer.Peer;

public class SpaceReclaimProtocol implements Runnable {

	private Peer peer = null;
	private int spaceToReclaim;
	
	public SpaceReclaimProtocol(Peer peer, int spaceToReclaim) {
		this.peer = peer;
		this.spaceToReclaim = spaceToReclaim;
	}
	
	@Override
	public void run() {
		ArrayList<ChunkInfo> chunksToClear = this.peer.getFilesManager().calcChunksToClear(spaceToReclaim);
		for(ChunkInfo chunkInfo : chunksToClear) {
			this.peer.getFilesManager().deleteChunk(chunkInfo.getFileId() + chunkInfo.getChunkNo());
			Message message = new Message();
			message.prepareMessage("REMOVED", "1.0", this.peer.getId(), chunkInfo.getFileId(), chunkInfo.getChunkNo(), -1, null);
			try {
				this.peer.getMCChannel().send(message.getHeader().getBytes("ISO-8859-1"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
