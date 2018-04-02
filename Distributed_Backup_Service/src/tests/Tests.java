package tests;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;

import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import filesmanager.ChunkInfo;
import peer.Peer;
import peer.UpdateTask;

public class Tests {

	public static void main(String[] args) {
		ArrayList<Peer> peers = new ArrayList<Peer>();

		int peerid = 1;
		try {
			peers.add(new Peer(peerid, "224.0.0.0", 8000, "224.0.0.0", 8001, "224.0.0.0", 8002));
			peerid++;
			peers.add(new Peer(peerid, "224.0.0.0", 8000, "224.0.0.0", 8001, "224.0.0.0", 8002));
			peerid++;
			peers.add(new Peer(peerid, "224.0.0.0", 8000, "224.0.0.0", 8001, "224.0.0.0", 8002));
			peerid++;
			//peers.add(new Peer(peerid, "224.0.0.0", 8000, "224.0.0.0", 8001, "224.0.0.0", 8002));
			//peerid++;
			//peers.add(new Peer(peerid, "224.0.0.0", 8000, "224.0.0.0", 8001, "224.0.0.0", 8002));
			//peers.get(0).backup("test.jpg", 2, true);
			//peers.get(1).restore("test.jpg", true);
			//peers.get(1).reclaim(0, false);
			peers.get(0).delete("test.jpg", true);

			long createdMillis = System.currentTimeMillis();
			
			while(true) {
				long nowMillis = System.currentTimeMillis();
				if((int)((nowMillis - createdMillis) / 1000) >= 7) {
					new Peer(peerid, "224.0.0.0", 8000, "224.0.0.0", 8001, "224.0.0.0", 8002);
					break;
				}
			}
	    
			//System.out.println(peers.get(2).state());

//			System.out.println(peers.get(1).getFilesManager().getChunksInfo().size());
/*
			for (BackedUpFileInfo file : peers.get(0).getFilesManager().getBackedUpFiles()) {
				for (ChunkInfo chunk : file.getBackedUpChunks()) {
					System.out.println("CHUNK " + chunk.getChunkNo() + " OWNERS:");
					for (int owner : chunk.getOwnerIds()) {
						System.out.println(owner);
					}
				}
			}
			for (ChunkInfo chunk : peers.get(4).getFilesManager().getChunksInfo()) {
				System.out.println("CHUNK " + chunk.getChunkNo() + " WITH " + chunk.getOwnerIds().size() + " OWNERS:");
				for (int owner : chunk.getOwnerIds()) {
					System.out.println(owner);
				}
			}
*/
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
