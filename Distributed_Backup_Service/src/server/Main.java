package server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import communications.RMIInterface;
import initiators.BackupInitiator;
import peer.FilesManager;
import peer.Peer;

public class Main {

	public static void main(String[] args) {
		
		Peer peer = new Peer(1);
		try {
			RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(peer, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Hello", stub);
		
			peer.backup("test.jpg", 2, false);
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
