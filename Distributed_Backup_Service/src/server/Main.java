package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
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
	private static String accessPoint;
	private static String MC_ip;
	private static int MC_port;
	private static String MDB_ip;
	private static int MDB_port;
	private static String MDR_ip;
	private static int MDR_port;
	
	public static void main(String[] args) {

		if(!validArguments(args))
			return;
	
		printArguments();
		
		//Create peer
		Peer peer = null;
		
		/*try {
			Peer peer = new Peer(serverId,"224.0.0.0",8000,"224.0.0.0",8001,"224.0.0.0",8002);
			if(serverId == 1) {
				peer.backup("test.jpg", 1, true);
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		try {
			peer = new Peer(serverId, MC_ip, MC_port, MDB_ip, MDB_port, MDR_ip, MDR_port);			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Error creating peer!");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error creating peer!");
			return;
		}
		
		//Enable peer for Remote Method Invoking     
        try {
        	RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(peer, 0);
        	// Bind the remote object's stub
			Naming.rebind(accessPoint, stub);
			System.out.println("Server ready for RMI communication \n \t IP address: " + InetAddress.getLocalHost().getHostAddress() + "\n \t local access point: " + accessPoint);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("RMI error");
			return;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Invalid access point");
			return;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Couldn't resolve this machine IP address");
		}

         while(true) {
        	//fazer qualquer coisa
         }       
	}
	
	private static boolean validArguments(String[] args) {
		if(args.length != 9) {
			System.out.println("User arguments error! \n \t Full usage: java Main <protocol_version> <server_id> <access_point> <MC_ip> <MC_port> <MDB_ip> <MDB_port> <MDR_ip> <MDR_port>");
			return false;
		}
		
		//Protocol version
		String versionPattern = "^1(\\.[0]{1,})?$";
		if(!args[0].matches(versionPattern)) {
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
		
		//RMI access point
		accessPoint = args[2];
		
		String ipPattern = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";
		
		//Multicast Control channel
		//IP
		if(!args[3].matches(ipPattern)) {
			System.out.println("Invalid Multicast Control channel IP error!");
			return false;
		}
		MC_ip = args[3];
		//Port
		try {
			MC_port = Integer.parseInt(args[4]);
			if(MC_port <= 0) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			System.out.println("Invalid Multicast Control channel port error! \n \t The port must be a positive integer");
			return false;
		}
		
		//Multicast Data Backup channel
		//IP
		if(!args[5].matches(ipPattern)) {
			System.out.println("Invalid Multicast Data Backup IP error!");
			return false;
		}
		MDB_ip = args[5];
		//Port
		try {
			MDB_port = Integer.parseInt(args[6]);
			if(MDB_port <= 0) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			System.out.println("Invalid Multicast Data Backup channel port error! \n \t The port must be a positive integer");
			return false;
		}
		
		//Multicast Data Recovery channel
		//IP
		if(!args[7].matches(ipPattern)) {
			System.out.println("Invalid Multicast Data Recovery channel IP error!");
			return false;
		}
		MDR_ip = args[7];
		//Port
		try {
			MDR_port = Integer.parseInt(args[8]);
			if(MDR_port <= 0) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			System.out.println("Invalid Multicast Data Recovery channel port error! \n \t The port must be a positive integer");
			return false;
		}
		
		//protocol version, serverId, accessPoint   (MC, MDB, MDR) -> ipAddress port
		return true;
	}
	
	private static void printArguments() {
		System.out.println("Protocol version: " + version);
		System.out.println("Server id: " + serverId);
		System.out.println("RMI access point: " + accessPoint);
		System.out.println("Multicast Control (MC) channel \n \t IP address: " + MC_ip + " \n \t port: " + MC_port);
		System.out.println("Multicast Data Backup (MDB) channel \n \t IP address: " + MDB_ip + " \n \t port: " + MDB_port);
		System.out.println("Multicast Data Recovery (MDR) channel \n \t IP address: " + MDR_ip + " \n \t port: " + MDR_port);
	}

}
