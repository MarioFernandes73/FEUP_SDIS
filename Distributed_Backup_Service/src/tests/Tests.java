package tests;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import peer.Peer;

public class Tests {

	public static void main(String[] args) {
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		
		int peerid = 1;
		try {
			peers.add(new Peer(peerid,"224.0.0.0",8000,"224.0.0.0",8001,"224.0.0.0",8002));
			peers.add(new Peer(peerid,"224.0.0.0",8000,"224.0.0.0",8001,"224.0.0.0",8002));
			peers.get(0).backup("test.jpg", 1, true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
