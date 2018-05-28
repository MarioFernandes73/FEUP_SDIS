package peer;

import java.util.TimerTask;

public class UpdateTask extends TimerTask {

    private Peer peer;

    public UpdateTask(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void run() {
        System.out.println("UPDATE TASK!");

        try {
            peer.saveAllInfo();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
