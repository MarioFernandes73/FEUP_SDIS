package peer;

import java.util.TimerTask;

public class UpdateTask extends TimerTask {

    private Peer peer;

    public UpdateTask(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void run() {

        try {
            System.out.println("Updated Info!");
            peer.saveAllInfo();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
