package protocols.protocols;

import messages.MessageBuilder;
import messages.Message;
import peer.Address;
import peer.ChunkInfo;
import peer.Peer;
import utils.Constants;

import java.util.HashMap;

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

        String[] msgArgs = new String[]{
                Constants.MessageType.PUT_CHUNK.toString(),
                this.peer.getId(),
                chunkInfo.getChunkId(),
                this.peer.getIP(),
                Integer.toString(this.peer.getPort())
        };
        Message msg = MessageBuilder.build(msgArgs);
        msg.setData(this.data);

        try{
            this.peer.sendFloodMessage(msg);
        } catch( Exception e){
            e.printStackTrace();
        }

        int tries = 0;
        while(tries < Constants.MAX_CHUNK_TRANSFER_TRIES){
            try{
                Thread.sleep(Constants.RESPONSE_AWAITING_TIME);
            } catch(Exception e){
                e.printStackTrace();
            }

            HashMap<String, Address> nextContacts = this.peer.getRecords().updateChunkInfoGetNextContacts(this.chunkInfo);

            //update chunkinfo
           // this.peer.getRecords().updateChunkInfoGetNextContacts();

            //verificar replication degree

            if (this.chunkInfo.getOwners().size() >= chunkInfo.getDesiredReplicationDeg()) {
                break;
            }

            tries++;
        }

    }
}
