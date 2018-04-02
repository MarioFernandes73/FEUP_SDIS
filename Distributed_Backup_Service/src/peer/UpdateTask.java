package peer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.TimerTask;

import communications.Message;
import filesmanager.BackedUpFileInfo;
import filesmanager.ChunkInfo;
import filesmanager.DeletedChunk;

public class UpdateTask extends TimerTask {

	private Peer peer = null;

	public UpdateTask(Peer peer) {
		this.peer = peer;
	}

	@Override
	public void run() {
		System.out.println("UPDATE TASK!");
		try {
			for (Message message : peer.getStoredMessages()) {
				peer.getFilesManager().updateChunkOwners(message);
			}
		} catch (Exception e) {

		} finally {
			this.peer.getFilesManager().saveChunksInfo();
			this.peer.getFilesManager().saveAllChunks();
			this.peer.getFilesManager().saveFilesInfo();
		}

		this.peer.getFilesManager().deleteFileChunks();
		for (DeletedChunk chunkInfo : this.peer.getChunksToDelete()) {
			for (BackedUpFileInfo backedUpFile : this.peer.getFilesManager().getBackedUpFiles()) {
				if (backedUpFile.getId().equals(chunkInfo.getInfo().getFileId())) {
					for (ChunkInfo backedUpChunkInfo : backedUpFile.getBackedUpChunks()) {
						if (backedUpChunkInfo.getChunkNo() == chunkInfo.getInfo().getChunkNo()) {

							Iterator<Integer> iter = backedUpChunkInfo.getOwnerIds().iterator();

							while (iter.hasNext()) {
								Integer owner = iter.next();

								if (owner == chunkInfo.getOwner()) {
									iter.remove();
								System.out.println("Initiator peer removed an owner from chunk "
										+ backedUpChunkInfo.getChunkId() + " due to file deletion");
								}
							}
						}
					}
				}
			}
		}

		for (BackedUpFileInfo backedUpFile : this.peer.getFilesManager().getBackedUpFiles()) {
			if (backedUpFile.getToDelete()) {
				boolean needMessage = false;
				for (ChunkInfo backedUpChunkInfo : backedUpFile.getBackedUpChunks()) {
					if (backedUpChunkInfo.getOwnerIds().size() != 0) {
						needMessage = true;
						break;
					}
				}
				if (needMessage) {
					System.out.println("SENT REDeletion message...");
					Message message = new Message();
					message.prepareMessage("INITDELETE", "2.0", peer.getId(), backedUpFile.getId(), -1, -1, null);
					try {
						this.peer.getMCChannel().send(message.getHeader().getBytes("ISO-8859-1"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}

	}

}
