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
	private static String filePath;
	private static int backupOperand;

	public static void main(String[] args) {
	
		//Extract arguments
		if (!validArguments(args)) {
			return;
		}
		

        String host = (args.length < 1) ? null : args[0];
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
			//this.peer_ac = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			System.out.println("Invalid access point error! Access point must be an integer User input: " + args[0]);
		}
		
		//Operation		
		try {
			//this.operation = Utils.operations.valueOf(args[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid operation error! \n \t Usage: BACKUP, RESTORE, DELETE, RECLAIM, BACKUPENH, RESTOREENH, DELETEENH, RECLAIMENH or STATE \n \t User input: " + args[1]);
			return false;
		}

		//parse third argument
		switch(operation) {
		case STATE:
			System.out.println("Performing STATE operation...");
			return true;
		case RECLAIM:
		case RECLAIMENH:
			try {
				if(args.length != 3) {
					//System.out.println("Wrong number of arguments for "+this.operation.name()+" operation! Full usage: java TestApp <peer_ap> "+this.operation.name()+" <reclaim_space>");
					return false;
				}
				diskSpace = Integer.parseInt(args[2]);			
				return true;
			} catch(NumberFormatException e) {
				System.out.println("Invalid operand error! \n \t Usage(ex): RECLAIM 0 \n \t User operand: " + args[2]);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Invalid operand error! \n \t Please specify the maximum amount of disk space (in KBs) that the service can use to store the chunks. \n \t Usage(ex): RECLAIM 0");
			}
			return false;
					
		case DELETE:
		case DELETEENH:
		case RESTORE:
		case RESTOREENH:
			if(args.length != 3) {
				//System.out.println("Wrong number of arguments for "+this.operation.name()+" operation! \n \t Full usage: java TestApp <peer_ap> "+this.operation.name()+" <file_name>");
			}
			
		default:
				try {
					filePath = args[2];				
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Invalid operand error! \n \t Please specify the path name of the file to backup/restore/delete \n \t Usage(ex): BACKUP file.txt");
				}
				break;
		}
		
		//parse fourth argument
		if((!operation.equals(Utils.operations.BACKUP)  && !operation.equals(Utils.operations.BACKUPENH)) && args.length > 3) {
			System.out.println("Too many arguments for " + operation.toString() + " operation! Performing operation using "+filePath+ " as file path.");
			return true;
		}
		
		//System.out.println(IPAddress);
		//System.out.println(port);
		System.out.println(operation);

		return true;
	}

}
