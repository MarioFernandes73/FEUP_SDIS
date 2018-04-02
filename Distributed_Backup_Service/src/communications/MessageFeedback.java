/**
 * 
 */
package communications;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import filesmanager.Chunk;
import filesmanager.ChunkInfo;
import peer.Peer;
import protocols.ChunkBackupProtocol;
import utils.Utils;

public class MessageFeedback implements Runnable {

	private Peer owner = null;
	private byte[] data = null;
	private Message message = null;

	public MessageFeedback(Peer owner, byte[] data) {
		this.data = data;
		this.owner = owner;
	}

	@Override
	public void run() {
		MessageInterpreter interpreter = new MessageInterpreter(data);
		this.message = interpreter.parseText();
		if (this.message == null) {
			System.out.println("Message received in the wrong format!");
		}
		switch (message.getOperation()) {
		case "PUTCHUNK":
			this.receivedPutchunkMessage();
			break;
		case "STORED":
			this.receivedStoredMessage();
			break;
		case "GETCHUNK":
			this.receivedGetchunkMessage();
			break;
		case "CHUNK":
			this.receivedChunkMessage();
		case "DELETE":
			this.receivedDeleteMessage();
			break;
		case "REMOVED":
			this.receivedRemovedMessage();
			break;
		default:
			return;
		}
	}

	/**
	 * Processes PUTCHUNK message by either disregarding it if it's the same user
	 * who sent it or by checking if the current peer can store the chunk and if so,
	 * saves it to the disk.
	 */

	private void receivedPutchunkMessage() {
		if (message.getSenderId() == owner.getId()) {
			return;
		}

		this.owner.getPutChunkMessages().add(message);

		ChunkInfo chunkInfo = new ChunkInfo(message.getFileId(), message.getChunkNo(), message.getReplicationDeg(),
				message.getBody().length);

		try {
			if (this.owner.getFilesManager().canSaveData(message.getBody().length)
					&& !this.owner.getFilesManager().hasChunk(chunkInfo.getFileId() + chunkInfo.getChunkNo())) {

				try {
					Thread.sleep(new Random().nextInt(Utils.MAX_RANDOM_DELAY));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (Message storedMessage : this.owner.getStoredMessages()) {
					if (storedMessage.getFileId().equals(message.getFileId())
							&& storedMessage.getChunkNo() == message.getChunkNo()
							&& !chunkInfo.getOwnerIds().contains(message.getSenderId())) {
						chunkInfo.getOwnerIds().add(storedMessage.getSenderId());
					}
				}

				if (chunkInfo.getOwnerIds().size() >= message.getReplicationDeg()) {
					return;
				}

				Message response = new Message();
				response.prepareMessage("STORED", message.getVersion(), owner.getId(), message.getFileId(),
						message.getChunkNo(), -1, null);
				System.out.println("MENSAGEM STORED ENVIADA POR " + owner.getId());
				chunkInfo.getOwnerIds().add(owner.getId());
				owner.getMCChannel().send(response.getHeader().getBytes("ISO-8859-1"));
				owner.getFilesManager().getChunksInfo().add(chunkInfo);
				owner.getFilesManager()
						.addChunkToSave(new Chunk(chunkInfo.getFileId() + chunkInfo.getChunkNo(), message.getBody()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes STORED message by either disregarding it if it's the same user who
	 * sent it or by adding it to the designated messages list, as well as checking
	 * if the current peer has the chunk on disk. If so, its replication degree is
	 * updated.
	 */

	private void receivedStoredMessage() {
		if (this.owner.getId() == this.message.getSenderId()) {
			return;
		}
		this.owner.getStoredMessages().add(this.message);
		this.owner.getFilesManager().updateChunkOwners(message);
		System.out
				.println("OWNER " + this.owner.getId() + " STORED MESSAGES: " + this.owner.getStoredMessages().size());
	}

	private void receivedGetchunkMessage() {
		if (this.owner.getId() == this.message.getSenderId()) {
			return;
		}
		if (this.owner.getFilesManager().hasChunk(this.message.getFileId() + this.message.getChunkNo())) {

			try {
				Thread.sleep(new Random().nextInt(Utils.MAX_RANDOM_DELAY));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (Message chunkMessage : this.owner.getChunkMessages()) {
				if (chunkMessage.getFileId().equals(message.getFileId())
						&& chunkMessage.getChunkNo() == message.getChunkNo()) {
					return;
				}
			}

			File existingChunk = this.owner.getFilesManager()
					.getExistingChunk(message.getFileId() + this.message.getChunkNo());
			Message response = new Message();
			try {
				response.prepareMessage("CHUNK", "1.0", this.owner.getId(), this.message.getFileId(),
						this.message.getChunkNo(), -1, Files.readAllBytes(existingChunk.toPath()));

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					baos.write(response.getHeader().getBytes());
					baos.write(response.getBody());
				} catch (IOException e) {
					e.printStackTrace();
				}

				byte[] payload = baos.toByteArray();

				this.owner.getMDRChannel().send(payload);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void receivedChunkMessage() {
		if (this.owner.getId() == this.message.getSenderId()) {
			return;
		}
		this.owner.getChunkMessages().add(this.message);
	}

	private void receivedDeleteMessage() {
		// delete de todos os chunks do disco -> funcao no filemanager
		// this.owner.getFilesManager().deleteAllChunks(this.message.getFileId());
	}

	private void receivedRemovedMessage() {
		if (this.owner.getId() == this.message.getSenderId()) {
			return;
		}
		this.owner.getPutChunkMessages().clear();
		for (ChunkInfo chunkInfo : this.owner.getFilesManager().getChunksInfo()) {
			if ((chunkInfo.getFileId() + chunkInfo.getChunkNo()).equals(message.getFileId() + message.getChunkNo())) {
				for (int i = 0; i < chunkInfo.getOwnerIds().size(); i++) {
					if (chunkInfo.getOwnerIds().get(i) == message.getSenderId()) {
						chunkInfo.getOwnerIds().remove(i);
						System.out.println("PEER " + this.owner.getId() + " REMOVED AN OWNER");
						if (chunkInfo.getDesiredReplicationDeg() > chunkInfo.getOwnerIds().size()) {
							System.out.println(
									"Chunk with not acceptable replication degree, starting to rebackup the chunk");
							try {
								Thread.sleep(new Random().nextInt(Utils.MAX_RANDOM_DELAY));
								for (Message putChunkMessage : this.owner.getPutChunkMessages()) {
									if ((putChunkMessage.getFileId() + putChunkMessage.getChunkNo())
											.equals(chunkInfo.getFileId() + chunkInfo.getChunkNo())) {
										System.out.println("Backup already started on another peer!");
										return;
									}
								}
								File existingChunk = this.owner.getFilesManager()
										.getExistingChunk(chunkInfo.getFileId() + chunkInfo.getChunkNo());
								byte[] existingChunkData = Files.readAllBytes(existingChunk.toPath());
								ChunkBackupProtocol reBackup = new ChunkBackupProtocol(this.owner, chunkInfo,
										existingChunkData);
								new Thread(reBackup).start();
								System.out.println("ReBackup started!");
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

	}

}
