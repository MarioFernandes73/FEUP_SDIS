package protocols;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import communications.Message;
import filesmanager.Chunk;
import filesmanager.ChunkInfo;
import peer.Peer;
import utils.Utils;

public class FileDeleteProtocol implements Runnable {

	private Peer peer = null;
	private String fileName;
	private boolean enhancement;

	public FileDeleteProtocol(Peer peer, String fileName, boolean enhancement) {
		this.peer = peer;
		this.fileName = fileName;
		this.enhancement = enhancement;
	}

	@Override
	public void run() {
		File existingFile = peer.getFilesManager().getExistingFile(fileName);
		String encryptedFileId = null;
		if (existingFile == null) {
			System.out.println("File doesn't exist.");
			return;
		}
		try {
			encryptedFileId = peer.getFilesManager().encryptFileId(existingFile);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.out.println("Error obtaining file id!");
			return;
		}
		
		Message message = new Message();
		String version = Utils.DEFAULT_VERSION;
		if(enhancement) {
			version = "2.0";
		}
		message.prepareMessage("DELETE", version, peer.getId(), encryptedFileId, -1, -1, null);
		try {
			peer.getMCChannel().send(message.getHeader().getBytes("ISO-8859-1"));
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("Error sending delete message!");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error sending delete message!");
			return;
		}
	}

}
