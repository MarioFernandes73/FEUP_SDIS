package protocols.initiators;

import filesmanager.BackedUpFileInfo;
import peer.Peer;

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

        //	pedir um backedupfilesinfo se nao tiver a alguem do
        BackedUpFileInfo backedUpFileInfo = this.peer.getBackedUpFileInfo(this.clientId, this.fileName);
        //	percorrer a lista de chunksinfo
        //	para cada chunk, ir buscar a address de cada owner, e mandar uma mensagem de DeleteChunk
        //		se obtiver todas as respostas necessarias
        //			anunciar que chunk tal, foi deletado e todos os outros dao update nos backedupfilesinfo deles
        //		se nao obtiver todas as respostas para determinado chunk
        //			anunciar quem fez delete, ou seja, os outros peers atualizam os owners daquele chunk daquele backedupfilesinfo
        //			mandar um announce que peer X quando se ligar a rede tem de fazer delete



    }
}
