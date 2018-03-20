package peer;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import channels.*;
import communications.RMIInterface;
import initiators.BackupInitiator;
import utils.Chunk;

public class Peer implements RMIInterface {
	
	private MulticastChannel MCChannel = null;
	private MulticastChannel MDBChannel = null;
	private MulticastChannel MDRChannel = null;
	private FilesManager filesManager = null;
	private int id;
	
	public Peer(int id) {
		this.id = id;
		this.filesManager = new FilesManager(this.id);
	}

	public int getId() {
		return id;
	}

	public FilesManager getFilesManager() {
		return filesManager;
	}
	
	public MulticastChannel getMCChannel() {
		return MCChannel;
	}

	public MulticastChannel getMDBChannel() {
		return MDBChannel;
	}

	public MulticastChannel getMDRChannel() {
		return MDRChannel;
	}

	@Override
	public String backup(String fileName, int replicationDegree, boolean enhancement) throws RemoteException {
		Thread thread = new Thread(new BackupInitiator(this, fileName, replicationDegree));
		thread.start();
		//handle thread
		
		
		//return thread response
		return null;
	}
	
	

}
