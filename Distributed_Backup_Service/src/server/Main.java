package server;

import java.rmi.RemoteException;

import initiators.BackupInitiator;
import peer.FilesManager;
import peer.Peer;

public class Main {

	public static void main(String[] args) {
		
		Peer peer = new Peer(1);
		
		
		try {
			peer.backup("test.jpg", 2, false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
