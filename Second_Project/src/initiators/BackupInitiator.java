package initiators;

import client.Client;
import peer.Peer;

import java.io.File;

public class BackupInitiator implements Runnable {

    private Client client;
    private Peer peer;
    private File file;
    private int replicationDegree;

    public BackupInitiator(Client client, Peer peer, File file, int replicationDegree) {
        this.client = client;
        this.peer = peer;
        this.file = file;
        this.replicationDegree = replicationDegree;
    }

    @Override
    public void run() {
       // String peer.getEncrypted(client, file);



    }


}
