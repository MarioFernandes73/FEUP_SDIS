package protocols.protocols;

import java.util.ArrayList;

import messages.MessageBuilder;
import messages.MessagesRecords;
import messages.Message;
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
    
    private Message createMessage() {
    	String[] msgArgs = new String[]{"PUTCHUNK"};
    	
    	return new MessageBuilder().build(msgArgs);
    }
    
    @Override
    public void run() {
        int tries = 0;
        while(tries < Constants.MAX_CHUNK_TRANSFER_TRIES){
            //construir mensagem
        	Message message = createMessage();
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

            if (this.chunkInfo.getOwners().size() >= chunkInfo.getDesiredReplicationDeg()) {
                break;
            }

            tries++;
        }

    }
}
