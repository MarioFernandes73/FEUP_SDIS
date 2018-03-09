package peer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import channels.*;
import utils.Chunk;
import utils.FilesManager;

public class Peer {
	
	public static void main(String[] args) {
		
		//PeerManager manager = new PeerManager(args);
		
		FilesManager filesmanager = new FilesManager(1);

		File file = new File("test2.png");
		
		ArrayList<Chunk> chunks = filesmanager.splitToChunks(file);
		
		for(Chunk chunk : chunks) {
			filesmanager.saveChunk(chunk);
		}
		
	}


	
	

}
