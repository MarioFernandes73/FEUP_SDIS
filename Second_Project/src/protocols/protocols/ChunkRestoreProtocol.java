package protocols.protocols;

import filesmanager.Chunk;
import peer.ChunkInfo;
import peer.Peer;

public class ChunkRestoreProtocol implements Runnable {

    private Peer peer;
    private ChunkInfo chunkInfo;
    private Chunk chunk;

    public ChunkRestoreProtocol(Peer peer, ChunkInfo chunkInfo) {
        this.peer = peer;
        this.chunkInfo = chunkInfo;
    }

    @Override
    public void run() {

    }

    public Chunk getChunk() {
        return this.chunk;
    }
}
