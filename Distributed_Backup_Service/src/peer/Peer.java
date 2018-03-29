package peer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Timer;

import channels.*;
import communications.RMIInterface;
import initiators.BackupInitiator;
import utils.Message;
import utils.UpdateTask;

public class Peer implements RMIInterface {
	
	private MulticastChannel MCChannel = null;
	private MulticastChannel MDBChannel = null;
	private MulticastChannel MDRChannel = null;
	private FilesManager filesManager = null;
	private int id;
	private ArrayList<Message> storedMessages = new ArrayList<Message>();
	
	public Peer(int id, String MCIP, int MCPort, String MDBIP, int MDBPort, String MDRIP, int MDRPort) throws UnknownHostException, IOException {
		this.id = id;
		this.filesManager = new FilesManager(this.id);
		this.MCChannel = new MulticastChannel(this, MCIP, MCPort);
		this.MDBChannel = new MulticastChannel(this, MDBIP, MDBPort);
		this.MDRChannel = new MulticastChannel(this, MDRIP, MDRPort);
		(new Thread(this.MCChannel)).start();
		(new Thread(this.MDBChannel)).start();
		(new Thread(this.MDRChannel)).start();
		
        Timer timer = new Timer();
        timer.schedule(new UpdateTask(this), 0, 5000);
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
		System.out.println("Starting to backup " + fileName);
		Thread thread = new Thread(new BackupInitiator(this, fileName, replicationDegree));
		thread.start();
		//handle thread
		
		//return thread response
		return null;
	}
	
	public ArrayList<Message> getStoredMessages() {
		return this.storedMessages;
	}

}
