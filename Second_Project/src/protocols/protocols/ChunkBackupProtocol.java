package protocols.protocols;

import messages.MessageBuilder;
import messages.Message;
import peer.Address;
import peer.ChunkInfo;
import peer.Peer;
import utils.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.ThreadLocalRandom.current;

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
        HashMap<String, Address> nextContacts = new HashMap<>();
        while(tries < Constants.MAX_CHUNK_TRANSFER_TRIES) {
            try {
                Thread.sleep(Constants.RESPONSE_AWAITING_TIME);

                nextContacts.putAll(this.peer.getRecords().updateChunkInfoGetNextContacts(this.chunkInfo));

                int currentRepDegree = this.chunkInfo.getDesiredReplicationDeg() - this.chunkInfo.getPerceivedReplicationDeg();

                if (currentRepDegree <= 0) {
                    break;
                }

                for (int i = 0; i < currentRepDegree; i++) {
                    int randomConnection = ThreadLocalRandom.current().nextInt(0, nextContacts.size());
                    int j = 0;
                    for (Map.Entry<String, Address> randomPeer : nextContacts.entrySet()) {
                        if (j == randomConnection) {
                            this.peer.sendMessageToAddress(randomPeer.getValue(), MessageBuilder.build(msgArgs));
                            nextContacts.remove(randomPeer.getKey());
                            break;
                        }
                        j++;
                    }
                }
                tries++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
