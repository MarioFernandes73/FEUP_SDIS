package peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

import utils.Utils;

public class Peer {

	private static InetAddress IPAddress;
	private static short port;
	private static Utils.operations operation;

	public static void main(String[] args) {

		if (!validArguments(args)) {
			return;
		}

		return;
	}

	private static boolean validArguments(String[] args) {
		if (args.length < 2 || args.length > 4) {
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
		
		try {
			operation = Utils.operations.valueOf(args[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid operation error! \n \t Usage: BACKUP, RESTORE, DELETE, RECLAIM, BACKUPENH, RESTOREENH, DELETEENH, RECLAIMENH or STATE \n \t User input: " + args[1]);
			return false;
		}

		System.out.println(IPAddress);
		System.out.println(port);
		System.out.println(operation);

		return true;
	}

}
