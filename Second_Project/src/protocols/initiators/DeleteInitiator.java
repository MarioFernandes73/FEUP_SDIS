package protocols.initiators;

import filesmanager.BackedUpFileInfo;
import messages.MessageBuilder;
import peer.ChunkInfo;
import peer.Peer;
import protocols.protocols.ChunkDeleteProtocol;
import utils.Constants;

import java.util.ArrayList;

public class DeleteInitiator implements Runnable {

    private Peer peer;
    private String clientId;
    private String fileName;

    public DeleteInitiator(Peer peer, String clientId, String fileName){
        this.peer = peer;
        this.clientId = clientId;
        this.fileName = fileName;
    }

    @Override
    public void run() {

        String fileId = "";
        try{
            fileId = this.peer.encryptFileName(this.fileName, this.clientId);
        } catch(Exception e){
            e.printStackTrace();
        }
        BackedUpFileInfo fileInfo = findBackedUpFileInfo(fileId);
        if(fileInfo != null){

            ArrayList<Thread> protocolThreads = new ArrayList<>();
            ArrayList<ChunkDeleteProtocol> protocols = new ArrayList<>();

            for(ChunkInfo chunkInfo: fileInfo.getBackedUpChunks()){
                ChunkDeleteProtocol protocol = new ChunkDeleteProtocol(this.peer, chunkInfo);
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

            for(ChunkDeleteProtocol protocol : protocols){
                if(protocol.getChunkInfo().getOwners().size() > 0){
                    System.out.println("CHUNK " + protocol.getChunkInfo().getChunkId() + " still has " + protocol.getChunkInfo().getOwners().size() + " owners");
                }
            }

        } else {
            System.out.println("File does not exist!");
        }

        //	pedir um backedupfilesinfo se nao tiver a alguem do
        //	percorrer a lista de chunksinfo
        //	para cada chunk, ir buscar a address de cada owner, e mandar uma mensagem de DeleteChunk
        //		se obtiver todas as respostas necessarias
        //			anunciar que chunk tal, foi deletado e todos os outros dao update nos backedupfilesinfo deles
        //		se nao obtiver todas as respostas para determinado chunk
        //			anunciar quem fez delete, ou seja, os outros peers atualizam os owners daquele chunk daquele backedupfilesinfo
        //			mandar um announce que peer X quando se ligar a rede tem de fazer delete



    }

    private BackedUpFileInfo findBackedUpFileInfo(String fileId){

        BackedUpFileInfo fileInfo = this.peer.getBackedUpFileInfo(fileId);
        if(fileInfo == null){
            String[] msgArgs = new String[]{
                    Constants.MessageType.SEND_FILE_INFO.toString(),
                    this.peer.getId(),
                    fileId
            };
            try{
                this.peer.sendFloodMessage(MessageBuilder.build(msgArgs));
                Thread.sleep(Constants.RESPONSE_AWAITING_TIME);
            } catch(Exception e){
                e.printStackTrace();
            }

            fileInfo = this.peer.getRecords().getFileInfo(fileId);
        }
        return fileInfo;
    }
}
