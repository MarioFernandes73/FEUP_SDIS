package initiators;

import peer.Peer;

public class RestoreInitiator implements Runnable {
	
	private Peer peer = null;
	private String fileName = null;

	public RestoreInitiator(Peer peer, String fileName) {
		this.peer = peer;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		if (peer.getFilesManager().checkIfFileExists(this.fileName)) {
			System.out.println("File already backed up!");
			return;
		}
	}

}
