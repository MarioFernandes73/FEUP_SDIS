package protocols.protocols;

import java.util.Date;
import java.util.Map.Entry;

import peer.*;

public class CheckContactsAlive implements Runnable {

    private final Peer peer;
    private final int secsToSleep = 5;
    private final int secsToDeclareDead = 20;

    public CheckContactsAlive(Peer peer)  {
        this.peer = peer;
    }
    
    @Override
    public void run() {
        while(true)
        {
        	try {
    			Thread.sleep(secsToSleep * 1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            for(Entry<String, TCPSendChannel> entry : peer.getForwardingTable().entrySet()) 
            {
            	long secondsOffset = (new Date().getTime() - entry.getValue().getLastTimeAlive().getTime())/1000;
            	if(secondsOffset > secsToDeclareDead)
            	{
            		peer.removePeer(entry.getKey());
            		ReplaceDeadConnection RDC = new ReplaceDeadConnection(peer);
                    new Thread(RDC).start();
            	}
            }
        }
    }
}
