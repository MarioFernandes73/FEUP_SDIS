package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MCChannel extends Thread{
	
	private InetAddress MCAST_ADDR = null;
	private int MCAST_PORT;
	
	public MCChannel(String address, int port) {
		try {
			MCAST_ADDR = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			
		}
		this.MCAST_PORT = port;
	}
	
	
    public void run() {
        System.out.println("Hello from a thread!");        
        
        
        

        
    }
}
