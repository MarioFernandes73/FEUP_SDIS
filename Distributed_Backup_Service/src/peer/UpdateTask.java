package peer;

import java.util.TimerTask;

import communications.Message;

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
			this.peer.getFilesManager().saveAllChunks();
			this.peer.getFilesManager().saveFilesInfo();
			this.peer.getFilesManager().deleteFileChunks();
		}
		
	}

}
