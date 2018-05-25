package protocols.protocols;

import java.util.Date;
import java.util.Map.Entry;

import peer.*;

public class CheckContactsAlive implements Runnable {

    private final Peer peer;
    private final int secsToSleep = 10;
    private final int secsToDeclareDead = 60;

    public CheckContactsAlive(Peer peer)  {
        this.peer = peer;
    }
    
    @Override
    public void run() {
        try {
			Thread.sleep(secsToSleep * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for(Entry<String, TCPSendChannel> entry : peer.getForwardingTable().entrySet()) 
        {
        	long secondsOffset = (entry.getValue().getLastTimeAlive().getTime() -  new Date().getTime())/1000;
        	if(secondsOffset > secsToDeclareDead)
        		peer.removePeer(entry.getKey());
        }
    }
}
