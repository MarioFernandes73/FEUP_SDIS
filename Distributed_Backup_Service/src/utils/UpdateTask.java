package utils;

import java.util.TimerTask;

import peer.Peer;

public class UpdateTask extends TimerTask {

	private Peer peer = null;
	
	public UpdateTask(Peer peer) {
		this.peer = peer;
	}
	
	@Override
	public void run() {
		System.out.println("UPDATE TASK!");
		try {
			for(Message message : peer.getStoredMessages()) {
				peer.getFilesManager().updateChunkOwners(message);
			}
		} catch (Exception e) {
			
		} finally {
			this.peer.getFilesManager().saveChunksInfo();
		}
		
	}

}
