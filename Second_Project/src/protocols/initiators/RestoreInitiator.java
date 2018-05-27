package protocols.initiators;

import client.Client;
import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import peer.ChunkInfo;
import peer.Peer;
import protocols.protocols.ChunkBackupProtocol;
import protocols.protocols.ChunkDeleteProtocol;
import protocols.protocols.ChunkRestoreProtocol;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class RestoreInitiator extends ProtocolInitiator implements Runnable {

    public RestoreInitiator(Peer peer, String clientId, String fileName) {
        super(peer, clientId, fileName);
    }

    @Override
    public void run() {

        ArrayList<Chunk> chunks = new ArrayList<>();

    	String fileId = "";
        try {
            fileId = peer.encryptFileName(fileName, clientId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BackedUpFileInfo fileInfo = findBackedUpFileInfo(fileId);
        if(fileInfo != null){
            ArrayList<Thread> protocolThreads = new ArrayList<>();
            ArrayList<ChunkRestoreProtocol> protocols = new ArrayList<>();

            for(ChunkInfo chunkInfo: fileInfo.getBackedUpChunks()){
                boolean cont = false;
                for(Chunk chunk : chunks){
                    if(chunk.getChunkId().equals(chunkInfo)){
                        cont = true;
                        break;
                    }
                }
                if(cont){
                    continue;
                }
                ChunkRestoreProtocol protocol = new ChunkRestoreProtocol(this.peer, chunkInfo);
                protocols.add(protocol);
                Thread thread = new Thread(protocol);
                protocolThreads.add(thread);
                thread.start();
            }

            for (Thread thread : protocolThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for(ChunkRestoreProtocol protocol : protocols){
                chunks.add(protocol.getChunk());
            }

        } else {
            System.out.println("File does not exist!");
        }
    }
}

