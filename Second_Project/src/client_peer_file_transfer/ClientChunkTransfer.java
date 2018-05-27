package client_peer_file_transfer;

import filesmanager.Chunk;
import peer.Peer;

public class ClientChunkTransfer implements Runnable{

    private String fileId;
    private Chunk chunk;
    private Peer peer;

    public ClientChunkTransfer(String fileId, Chunk chunk, Peer peer){
        this.fileId = fileId;
        this.chunk = chunk;
        this.peer = peer;
    }

    @Override
    public void run() {
        if(peer.chunkTransferDuplicated(this.fileId, this.chunk)) {
            return;
        }

        peer.addClientTransferChunk(this.fileId, this.chunk);
    }
}
