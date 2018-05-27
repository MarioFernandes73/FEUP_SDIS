package protocols.initiators;

import client.Client;
import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import messages.MessageBuilder;
import peer.ChunkInfo;
import peer.Peer;
import protocols.protocols.ChunkBackupProtocol;
import utils.Constants;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class BackupInitiator extends ProtocolInitiator implements Runnable {

    private String clientId;
    private File file;
    private int replicationDegree;

    public BackupInitiator(Peer peer, String clientId, String fileName, File file, int replicationDegree) {
        super(peer, clientId, fileName);
        this.replicationDegree = replicationDegree;
    }

    @Override
    public void run() {
        String encryptedFileId = null;
        try {
            encryptedFileId = peer.encryptFileName(fileName, clientId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        ArrayList<Chunk> chunks = this.peer.splitToChunks(file);
        ArrayList<ChunkInfo> chunksInfo = new ArrayList<>();
        ArrayList<Thread> protocolThreads = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            ChunkInfo chunkInfo = new ChunkInfo(encryptedFileId, i, replicationDegree, chunks.get(i).getData().length);
            chunksInfo.add(chunkInfo);
            Thread thread = new Thread(new ChunkBackupProtocol(this.peer, chunkInfo, chunks.get(i).getData()));
            protocolThreads.add(thread);
            this.peer.clearStoredMessagesOfFile(encryptedFileId);
            thread.start();
        }

        for (Thread thread : protocolThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(ChunkInfo chunkInfo: chunksInfo) {
            if(chunkInfo.getOwners().size() > 0) {

                if(chunkInfo.getOwners().size() >= this.replicationDegree) {
                    System.out.println("Successful backup of chunk "+ chunkInfo.getChunkNo());
                } else {
                    System.out.println("Successful backup of chunk " + chunkInfo.getChunkNo() +" but with a replication degree below the threshold");
                }
                System.out.println("Desired replication degree: " + this.replicationDegree);
                System.out.println("Current replication degree: " + chunkInfo.getOwners().size());
            } else {
                System.out.println("Unsuccessful backup of chunk " + chunkInfo.getChunkNo());
            }
        }



        BackedUpFileInfo newBackedUpFile = new BackedUpFileInfo(encryptedFileId, file.getName(), chunksInfo);
        this.peer.saveBackedUpFileInfo(newBackedUpFile);
        String[] msgArgs2 = new String[]{
                Constants.MessageType.SEND_BACKED_UP_FILE_INFO.toString(),
                this.peer.getId(),
                newBackedUpFile.toString()
        };
        try {
            this.peer.sendFloodMessage(MessageBuilder.build(msgArgs2));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
