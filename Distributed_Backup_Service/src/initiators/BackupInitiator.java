package initiators;

import java.io.File;
import java.util.ArrayList;

import peer.Peer;
import utils.Chunk;
import utils.FileInfo;
import utils.Utils;

public class BackupInitiator implements Runnable {
	
	private ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	private Peer peer = null;
	private String fileName = null;
	private int replicationDegree = 0;
	
	
	public BackupInitiator(Peer peer, String fileName, int replicationDegree) {
		this.peer = peer;
		this.fileName = fileName;
		this.replicationDegree = replicationDegree;
	}
	
	@Override
	public void run() {
		this.peer.getFilesManager().saveInfo();
	}
	
}
