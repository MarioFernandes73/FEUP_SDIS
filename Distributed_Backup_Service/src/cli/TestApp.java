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

	private static InetAddress IPAddress;
	private static short port;
	private static Utils.operations operation;
	private static int diskSpace;
	private static String filePath;
	private static int backupOperand;

	public static void main(String[] args) {

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

		// parse first argument

		try {
			if (args[0].contains(":")) {
				String[] peerAP = args[0].split(":");
				IPAddress = InetAddress.getByName(peerAP[0]);
				port = (short) Integer.parseInt(peerAP[1]);
			} else {
				IPAddress = InetAddress.getByName(Utils.defaultIP);
				port = (short) Integer.parseInt(args[0]);
			}
		} catch (UnknownHostException e) {
			System.out.println(
					"Invalid IP error! \n \t Usage: <IP address>:<port number> or <port number> as the first argument. \n \t "
							+ e.getMessage());
			return false;
		} catch (NumberFormatException e) {
			System.out.println(
					"Invalid port error! \n \t Usage: <IP address>:<port number> or <port number> as the first argument. \n \t Only numbers allowed on the port input. \n \t "
							+ e.getMessage());
			return false;
		}
		
		//parse second argument
		
		try {
			operation = Utils.operations.valueOf(args[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid operation error! \n \t Usage: BACKUP, RESTORE, DELETE, RECLAIM, BACKUPENH, RESTOREENH, DELETEENH, RECLAIMENH or STATE \n \t User input: " + args[1]);
			return false;
		}

		//parse third argument
		switch(operation) {
		case STATE:
			if(args.length > 2) {
				System.out.println("Too many arguments for STATE operation! Performing simple STATE operation... \n \t Full usage: java TestApp <peer_ap> STATE");
			}
			return true;
		case RECLAIM:
		case RECLAIMENH:
			try {
				diskSpace = Integer.parseInt(args[2]);
				if(args.length > 3) {
					System.out.println("Too many arguments for RECLAIM operation! Performing operation using "+diskSpace+" disk space... \n \t Full usage: java TestApp <peer_ap> RECLAIM <opnd_1>");
					}
				return true;
			} catch(NumberFormatException e) {
				System.out.println("Invalid operand error! \n \t Usage(ex): RECLAIM 0 \n \t User operand: " + args[2]);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Invalid operand error! \n \t Please specify the maximum amount of disk space (in KBs) that the service can use to store the chunks. \n \t Usage(ex): RECLAIM 0");
			}
			return false;
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
		
		System.out.println(IPAddress);
		System.out.println(port);
		System.out.println(operation);

		return true;
	}

}
