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
	private static String version;
	private static int serverId;
	private static int accessPoint;
	private static Pair<Integer, String> MC;
	private static Pair<Integer, String> MDB;
	private static Pair<Integer, String> MDR;
	
	public static void main(String[] args) {

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
		if(!validArguments(args))
			return;
		
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
		
	}
	
	private static boolean validArguments(String[] args) {
		if(args.length != 9) {
			System.out.println("User arguments error! \n \t Full usage: java Main <protocol_version> <server_id> <access_point> <MC_ip> <MC_port> <MDB_ip> <MDB_port> <MDR_ip> <MDR_port>");
			return false;
		}
		
		//Protocol version
		String versionPattern = "^1(\\.[0]{1,})?$";
		if(!args[1].matches(versionPattern)) {
			System.out.println("Invalid version error! \n \t The only allowed version is 1.0");
			return false;
		}
		version = "1.0";
		
		//Server Id
		try {
			serverId = Integer.parseInt(args[1]);
			if(serverId < 0) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			System.out.println("Invalid server id error! \n \t The server id must be a non-negative integer");
			return false;
		}
		
		//Server access point
		try {
			accessPoint = Integer.parseInt(args[2]);
			if(accessPoint <= 0) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			System.out.println("Invalid access point error! \n \t The access point must be a positive integer");
			return false;
		}
		
		//Multicast Control channel
		
		//Multicast Data Backup channel
		
		//Multicast Data Recovery channel
		
		//protocol version, serverId, accessPoint   (MC, MDB, MDR) -> ipAddress port
		return true;
	}

}
