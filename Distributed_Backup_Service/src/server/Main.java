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
import utils.MessageInterpreter;

public class Main {

	public static void main(String[] args) {
	
		String test = "PUTCHUNK 1.0 1 333333333333333333333a333333333333333333333333333333333333333333 1 3 \r\n\r\nola";
		MessageInterpreter task = new MessageInterpreter(test);
		Thread t = new Thread(task);
		try {
			t.start();
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println(task.getMessage().getBody());			
		}

		/*
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
*/
	}

}
