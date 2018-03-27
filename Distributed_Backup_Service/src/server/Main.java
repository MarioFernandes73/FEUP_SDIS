package server;

import java.io.IOException;
import java.net.UnknownHostException;
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
/*	
		String test = "PUTCHUNK 1.0 1 333333333333333333333a333333333333333333333333333333333333333333 1 3 \r\n\r\nola";
		MessageInterpreter task = new MessageInterpreter(test);
		Thread t = new Thread(task);
		try {
			t.start();
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(task.getMessage() != null)
			System.out.println(task.getMessage().getBody());			
		}
*/
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
		int peerid = Integer.parseInt(args[0]);
		try {
			Peer peer = new Peer(peerid,"224.0.0.0",8000,"224.0.0.0",8001,"224.0.0.0",8002);
			if(peerid == 1) {
				peer.backup("test.jpg", 1, true);
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("bye bye");
		peerid-=1;
		
	}

}
