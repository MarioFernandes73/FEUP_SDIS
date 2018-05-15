package protocols.protocols;

import messages.MessagesRecords;
import messages.responses.MessageStored;
import peer.ChunkInfo;
import peer.Peer;
import utils.Constants;

public class ChunkBackupProtocol implements Runnable {

    private final Peer peer;
    private final ChunkInfo chunkInfo;
    private final byte[] data;

    public ChunkBackupProtocol(Peer peer, ChunkInfo chunkInfo, byte[] data)  {
        this.peer = peer;
        this.chunkInfo = chunkInfo;
        this.data = data;
    }

    @Override
    public void run() {
        int tries = 0;
        while(tries < Constants.MAX_CHUNK_TRANSFER_TRIES){
            //construir mensagem

            //mandar

            //esperar
            try{
                Thread.sleep(Constants.RESPONSE_AWAITING_TIME);
            } catch(Exception e){
                e.printStackTrace();
            }

            //update chunkinfo
           // this.peer.getRecords().updateChunkInfoStoredMessages();

            //verificar replication degree

            if (this.chunkInfo.getOwnerIds().size() >= chunkInfo.getDesiredReplicationDeg()) {
                break;
            }

            tries++;
        }

    }
}
