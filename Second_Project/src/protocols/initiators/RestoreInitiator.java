package protocols.initiators;

import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import p.ChunkInfo;
import p.Peer;
import protocols.protocols.ChunkRestoreProtocol;

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
                if(protocol.getChunk() == null){
                    return;
                }
                chunks.add(protocol.getChunk());
            }

            if(fileInfo.getBackedUpChunks().size() == chunks.size()){
                this.peer.addClientTransferChunks(fileId, chunks);
            }


        } else {
            System.out.println("File does not exist!");
        }
    }
}

