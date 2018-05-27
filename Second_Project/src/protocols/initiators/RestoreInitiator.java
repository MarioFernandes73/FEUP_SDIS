package protocols.initiators;

import client.Client;
import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import peer.ChunkInfo;
import peer.Peer;
import protocols.protocols.ChunkBackupProtocol;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class RestoreInitiator extends ProtocolInitiator implements Runnable {

    public RestoreInitiator(Peer peer, String clientId, String fileName) {
        super(peer, clientId, fileName);
    }

    @Override
    public void run() {
    	String fileId = "";
        try {
            fileId = peer.encryptFileName(fileName, clientId);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BackedUpFileInfo fileInfo = findBackedUpFileInfo(fileId);
        if(fileInfo != null){



        } else {
            System.out.println("File does not exist!");
        }
    }
}

