package tests;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import filesmanager.BackedUpFileInfo;
import filesmanager.Chunk;
import filesmanager.ChunkInfo;
import peer.Peer;

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
			peers.add(new Peer(peerid, "224.0.0.0", 8000, "224.0.0.0", 8001, "224.0.0.0", 8002));
			peerid++;
			peers.add(new Peer(peerid, "224.0.0.0", 8000, "224.0.0.0", 8001, "224.0.0.0", 8002));
			//peers.get(0).backup("test.jpg", 2, true);

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

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
