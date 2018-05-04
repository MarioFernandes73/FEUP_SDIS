package initiators;

import peer.Peer;

import java.io.File;

public class BackupInitiator implements Runnable {

    private Peer peer;
    private File file;
    private int replicationDegree;
    private boolean enhancement;

    public BackupInitiator(Peer peer, File file, int replicationDegree, boolean enhancement) {
        this.peer = peer;
        this.file = file;
        this.replicationDegree = replicationDegree;
        this.enhancement = enhancement;
    }

    @Override
    public void run() {
        //check if file exist



    }


}
