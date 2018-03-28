package cli;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import communications.RMIInterface;
import utils.Utils;

public class TestApp {

	private static int peer_ac;
	private static Utils.operations operation;
	private static int diskSpace;
	private static String fileName;
	private static String filePath;
	private static int replicationDeg;

	public static void main(String[] args) {
	
		//Extract arguments
		if (!validArguments(args)) {
			return;
		}
		
		//print arguments
		printArguments();
		
        Registry registry;
		try {
			registry = LocateRegistry.getRegistry(host);
	        RMIInterface stub = (RMIInterface) registry.lookup("Hello");
	        String response = stub.backup("test.jpg", 3, false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}

	private static boolean validArguments(String[] args) {
		if (args.length < 2 || args.length > 4) {
			System.out.println("User arguments error! \n \t Full usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
			return false;
		}

		//Peer access point
		try {
			peer_ac = Integer.parseInt(args[0]);
			if(peer_ac <= 0) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			System.out.println("Invalid access point error! Access point must be an integer User input: " + args[0]);
			return false;
		}
		
		//Operation		
		try {
			this.operation = Utils.operations.valueOf(args[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid operation error! \n \t Usage: BACKUP, RESTORE, DELETE, RECLAIM, BACKUPENH, RESTOREENH, DELETEENH, RECLAIMENH or STATE \n \t User input: " + args[1]);
			return false;
		}

		//parse operation arguments
		switch(operation) {
		
		case STATE:
			System.out.println("Performing STATE operation...");
			return true;
			
		case RECLAIM:
		case RECLAIMENH:
			if(args.length != 3) {
				System.out.println("Wrong number of arguments for "+this.operation.name()+" operation! Full usage: java TestApp <peer_ap> "+this.operation.name()+" <reclaim_space>");
				return false;
			}
			try {				
				diskSpace = Integer.parseInt(args[2]);	
				if(diskSpace < 0) throw new NumberFormatException();
				
				return true;
			} catch(NumberFormatException e) {
				System.out.println("Invalid disk space error for "+this.operation.name()+" operation! \n \t User input: " + args[2]);
				return false;
			}	
					
		case DELETE:
		case DELETEENH:
		case RESTORE:
		case RESTOREENH:
			if(args.length != 3) {
				System.out.println("Wrong number of arguments for "+this.operation.name()+" operation! \n \t Full usage: java TestApp <peer_ap> "+this.operation.name()+" <file_name>");
				return false;
			}
			fileName = args[2];
			return true;
			
		case BACKUP:
		case BACKUPENH: 
			if(args.length != 4) {
				System.out.println("Wrong number of arguments for "+this.operation.name()+" operation! \n \t Full usage: java TestApp <peer_ap> "+this.operation.name()+" <file_name> <replication_degree>");
				return false;
			}
			
			fileName = args[2];
			
			try {				
				replicationDeg = Integer.parseInt(args[3]);	
				if(replicationDeg <= 0) throw new NumberFormatException();
				
				return true;
			} catch(NumberFormatException e) {
				System.out.println("Invalid replication degree error! \n \t User operand: " + args[3]);
				return false;
			} 
			
		default:
			System.out.println("Invalid operation");
			return false;
		}

		return false;
	}
	
	private static void printArguments(){
		System.out.println("Peer RMI access point: "+ peer_ac);
		
		System.out.println("Operation: "+ operation.name());
		
		switch(operation) {
			
		case RECLAIM:
		case RECLAIMENH:	
			if(diskSpace == 0 || diskSpace >= MAX_DISK_SPACE)
				System.out.println("All "+ Utils.MAX_DISK_SPACE+" bytes have been reclaimed from disk (100%)");			
			else	
				System.out.println(Utils.MAX_DISK_SPACE+" bytes have been reclaimed from disk"+ diskSpace/Utils.MAX_DISK_SPACE);
			
		case DELETE:
		case DELETEENH:
		case RESTORE:
		case RESTOREENH:
			System.out.println("File: "+fileName);
			
		case BACKUP:
		case BACKUPENH: 
			System.out.println("File: "+fileName);
			System.out.println("Replication degree: "+replicationDeg);
			fileName = args[2];
		}
	}
}
