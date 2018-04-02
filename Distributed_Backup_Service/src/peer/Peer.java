package peer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Timer;

import communications.Message;
import communications.MulticastChannel;
import communications.RMIInterface;
import filesmanager.FilesManager;
import initiators.BackupInitiator;
import initiators.RestoreInitiator;
import protocols.FileDeleteProtocol;
import utils.Utils;
import protocols.SpaceReclaimProtocol;

public class Peer implements RMIInterface {
	
	private MulticastChannel MCChannel = null;
	private MulticastChannel MDBChannel = null;
	private MulticastChannel MDRChannel = null;
	private FilesManager filesManager = null;
	private int id;
	private ArrayList<Message> storedMessages = new ArrayList<Message>();
	private ArrayList<Message> chunkMessages = new ArrayList<Message>();
	private ArrayList<Message> putChunkMessages = new ArrayList<Message>();
	
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
        timer.schedule(new UpdateTask(this), 5000, 5000);
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
		return null;
	}
	
	public String restore(String fileName, boolean enhancement) throws RemoteException {
		System.out.println("Starting to restore " + fileName);
		Thread thread = new Thread(new RestoreInitiator(this, fileName));
		thread.start();
		return null;
	}
	
	public ArrayList<Message> getStoredMessages() {
		return this.storedMessages;
	}
	
	public ArrayList<Message> getChunkMessages() {
		return this.chunkMessages;
	}
	
	@Override
	public String delete(String fileName, boolean enhancement) throws RemoteException {
		System.out.println("Starting to delete " + fileName);
		Thread thread = new Thread(new FileDeleteProtocol(this, fileName));
		thread.start();
		return null;
	}

	@Override
	public String reclaim(int space, boolean enhancement) throws RemoteException {
		System.out.println("Starting to reclaim " + space + " KBs");
		Thread thread = new Thread(new SpaceReclaimProtocol(this, space));
		thread.start();
		return null;
	}

	@Override
	public String state() throws RemoteException {
		String state = filesManager.getState();
		state += "\nStorage information: ";
		state += "Max disc space: " + Utils.MAX_DISK_SPACE/1000 + " KBytes\n";
		state += "Free disc space: " + filesManager.getCurrentDiskSpace()/1000 + " KBytes (" + filesManager.getCurrentDiskSpace()*100/Utils.MAX_DISK_SPACE + "%)";
		int chunkSpace = Utils.MAX_DISK_SPACE - filesManager.getCurrentDiskSpace();
		state += "Chunk occupation space: " + chunkSpace/1000 + " KBytes (" + chunkSpace*100/Utils.MAX_DISK_SPACE + "%)";
		return state;
	}

	public ArrayList<Message> getPutChunkMessages() {
		return this.putChunkMessages;
	}

}
