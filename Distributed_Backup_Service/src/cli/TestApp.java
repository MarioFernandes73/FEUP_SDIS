package cli;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import communications.RMIInterface;
import utils.Utils;

public class TestApp {

	private static String accessPoint;
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
		
		//Perform operation on RMI object
		/*try {
	        RMIInterface stub = (RMIInterface) Naming.lookup(accessPoint);
	        String response = stub.backup("test.jpg", 3, false);
	        System.out.println(response);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Wrong access point");
		}*/
		
		//Perform operation on RMI object
		RMIInterface stub;
        try {
			stub = (RMIInterface) Naming.lookup(accessPoint);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			System.out.println("Error accessing RMI object");
			return;
		} catch(MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Invalid RMI access point");
			return;
		}

        boolean enhance = operation.name().endsWith("ENH");    
        String response = "";
        try {
	        switch(operation) {
		        case BACKUP:
		        case BACKUPENH:
					response = stub.backup(fileName, replicationDeg, enhance);
				
		        	break;
		        	
		        case RESTORE:
		        case RESTOREENH:
		        	response = stub.restore(fileName, enhance);
		        	break;
		        	
		        case DELETE:
		        case DELETEENH:
		        	response = stub.delete(fileName, enhance);
		        	break;
		        	
		        case RECLAIM:
		        case RECLAIMENH:
		        	response = stub.reclaim(diskSpace, enhance);
		        	break;
		        	
		        case STATE:
		        	response = stub.state();
		        	break;
	        }
        } catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("Error invoking method remotely");
		}
        //String response = stub.backup("test.jpg", 3, false);
        
        System.out.println(response);
		return;
	}

	private static boolean validArguments(String[] args) {
		if (args.length < 2 || args.length > 4) {
			System.out.println("User arguments error! \n \t Full usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
			return false;
		}

		//Peer access point
		accessPoint = args[0];
		
		//Operation		
		try {
			operation = Utils.operations.valueOf(args[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid operation error! \n \t Allowed operation: BACKUP, RESTORE, DELETE, RECLAIM, BACKUPENH, RESTOREENH, DELETEENH, RECLAIMENH or STATE \n \t User input: " + args[1]);
			return false;
		}

		//parse operation arguments
		switch(operation) {
		
		case STATE:
			return true;
			
		case RECLAIM:
		case RECLAIMENH:
			if(args.length != 3) {
				System.out.println("Wrong number of arguments for "+operation.name()+" operation! Full usage: java TestApp <peer_ap> "+operation.name()+" <reclaim_space>");
				return false;
			}
			try {				
				diskSpace = Integer.parseInt(args[2]);	
				if(diskSpace < 0) throw new NumberFormatException();
				
				return true;
			} catch(NumberFormatException e) {
				System.out.println("Invalid disk space error for "+operation.name()+" operation! \n \t User input: " + args[2]);
				return false;
			}	
			
		case DELETE:
		case DELETEENH:
		case RESTORE:
		case RESTOREENH:
			if(args.length != 3) {
				System.out.println("Wrong number of arguments for "+operation.name()+" operation! \n \t Full usage: java TestApp <peer_ap> "+operation.name()+" <file_name>");
				return false;
			}
			fileName = args[2];
			return true;
			
		case BACKUP:
		case BACKUPENH: 
			if(args.length != 4) {
				System.out.println("Wrong number of arguments for "+operation.name()+" operation! \n \t Full usage: java TestApp <peer_ap> "+operation.name()+" <file_name> <replication_degree>");
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
	}
	
	private static void printArguments() {
		System.out.println("Peer RMI access point: "+ accessPoint);
		
		System.out.println("Operation: "+ operation.name());
		
		switch(operation) {
			
		case RECLAIM:
		case RECLAIMENH:	
			if(diskSpace == 0 || diskSpace >= Utils.MAX_DISK_SPACE)
				System.out.println("All "+ Utils.MAX_DISK_SPACE+" bytes have been reclaimed from disk (100%)");			
			else	
				System.out.println(diskSpace+" bytes have been reclaimed from disk ("+ diskSpace*100/Utils.MAX_DISK_SPACE+"%)");
			break;
			
		case DELETE:
		case DELETEENH:
		case RESTORE:
		case RESTOREENH:
			System.out.println("File name: "+fileName);
			break;
			
		case BACKUP:
		case BACKUPENH: 
			System.out.println("File name: "+fileName);
			System.out.println("Replication degree: "+replicationDeg);
			break;
		}
	}
}