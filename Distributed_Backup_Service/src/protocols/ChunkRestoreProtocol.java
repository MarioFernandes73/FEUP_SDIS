package protocols;

import filesmanager.Chunk;
import peer.Peer;

public class ChunkRestoreProtocol implements Runnable{

	private Peer peer = null;
	private Chunk chunk = null;
	
	public ChunkRestoreProtocol(Peer peer) {
		this.peer = peer;
	}

	@Override
	public void run() {
		
	}
	
	public Chunk getChunk() {
		return this.chunk;
	}
	
}
