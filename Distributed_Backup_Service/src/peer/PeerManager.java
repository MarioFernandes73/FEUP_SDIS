package peer;

import java.io.IOException;

import channels.MulticastChannel;

public class PeerManager {

	private MulticastChannel MCChannel = null;
	private MulticastChannel MDBChannel = null;
	private MulticastChannel MDRChannel = null;

	public PeerManager(String[] args) {
		initializeThreads(args);
	}
	
	
	private void initializeThreads(String args[]) {
		
		try {
			MCChannel = (new MulticastChannel(args[0], Integer.parseInt(args[1])));
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.out.println("goodbye");
		
	}

}
