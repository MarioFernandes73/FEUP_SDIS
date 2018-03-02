package peer;

import java.io.IOException;

import channels.MCChannel;
import channels.MDBChannel;
import channels.MDRChannel;

public class PeerManager {

	private MDBChannel mdbChannel = null;
	private MCChannel mcChannel = null;
	private MDRChannel mdrChannel = null;

	public PeerManager(String[] args) {
		// TODO Auto-generated constructor stub
	}
	
	
	private void initializeThreads() {
		
		try {
			mdbChannel = (new MDBChannel(args[0], Integer.parseInt(args[2])));
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mdbChannel.start();
		
		try {
			mdbChannel.send("ola");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mdbChannel.receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
