package protocols.initiators;

import client.Client;
import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import peer.ChunkInfo;
import peer.Peer;
import protocols.protocols.ChunkBackupProtocol;

import java.io.File;
import java.util.ArrayList;

public class RestoreInitiator implements Runnable {

    private Client client;
    private Peer peer;
    private File file;
    private int replicationDegree;
    private String fileId;

    public RestoreInitiator(Peer peer, String fileId) {
        this.peer = peer;
        this.fileId = fileId;
    }

    @Override
    public void run() {

        //Restore deve guardar, no clientTransferedChunks do peer, os chunks restaurados para serem posteriormente transferidos para o cliente
    	BackedUpFileInfo fileInfo = peer.getBackedUpFileInfo(fileId);

        ArrayList<Chunk> chunks = this.peer.splitToChunks(file);
        ArrayList<ChunkInfo> chunksInfo = new ArrayList<>();
        ArrayList<Thread> protocolThreads = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            ChunkInfo chunkInfo = new ChunkInfo(fileId, i, replicationDegree, chunks.get(i).getData().length);
            chunksInfo.add(chunkInfo);
            Thread thread = new Thread(new ChunkBackupProtocol(this.peer, chunkInfo, chunks.get(i).getData()));
            protocolThreads.add(thread);
            this.peer.clearStoredMessagesOfFile(fileId);
            thread.start();
        }

        for (Thread thread : protocolThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
/*
        for(ChunkInfo chunkInfo: chunksInfo) {
            if(chunkInfo.getOwnerIds().size() > 0) {

                BackedUpFileInfo newBackedUpFile = new BackedUpFileInfo(fileId, file.getFileName(), file.lastModified(), true);
                newBackedUpFile.getBackedUpChunks().addAll(chunksInfo);
                this.peer.updateBackedUpFiles(newBackedUpFile);

                if(chunkInfo.getOwnerIds().size() >= this.replicationDegree) {
                    System.out.println("Successful backup of chunk "+ chunkInfo.getChunkNo());
                } else {
                    System.out.println("Successful backup of chunk " + chunkInfo.getChunkNo() +" but with a replication degree below the threshold");
                }
                System.out.println("Desired replication degree: " + this.replicationDegree);
                System.out.println("Current replication degree: " + chunkInfo.getOwnerIds().size());
            } else {
                System.out.println("Unsuccessful backup of chunk " + chunkInfo.getChunkNo());
            }
        }
        */
    }


}

