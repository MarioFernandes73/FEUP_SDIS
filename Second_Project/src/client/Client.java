package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import client_peer_file_transfer.PartitionedFile;
import rmi.RMIInterface;
import utils.Constants;

public class Client {

    private String id;
    private String name;
	private String peerAccessPoint;
	private String selfAccessPoint;
	private String ip;
	private String port;
	private Constants.Operation operation;
	private String fileName;
	private int replicationDeg;
	private RMIInterface stub;
	
    public Client(String args[]) throws Exception {
        if (!verifyArgs(args)) {
    		throw new Exception();
		}

		if (!getPeerRMIStub()) {
    		throw new Exception();
		}

		calculateClientId();
		printInfo();

		if (!performStubOperation()) {
			throw new Exception();
		}
    }

	private boolean verifyArgs(String args[]) {
		// <online/lan> <client_port> <peer_rmi_access_point> <operation> <file_name/file_path> <op_arg2>

    	if(args.length < 5 || args.length > 6) {
			System.out.println("ERROR! Invalid number of arguments. \tCorrect usage: // <online/lan> <client_port> <peer_rmi_access_point> <operation> <file_name/file_path> <op_arg2>");
			return false;
    	}

		// <online/lan>
    	switch(args[0]) {
		case "online":
			try {
				this.ip = getPublicIP();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("ERROR! Couldn't get public IP address.");
			}
			break;

		case "lan":
			try {
				this.ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.println("ERROR! Couldn't get local IP address.");
			}
			break;

		default:
			System.out.println("ERROR! Invalid network type. First argument should be 'online' or 'lan'.");
			return false;
		}

		// <client_port>
		if (args[1].matches("[0-9]{1,5}")) {
			this.port = args[1];
		} else {
			System.out.println("ERROR! Invalid port.");
			return false;
		}

		// <peer_rmi_access_point>
		this.peerAccessPoint = args[2];

    	// <operation>
		try {
			this.operation = Constants.Operation.valueOf(args[3].toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("ERROR! Invalid operation. \tValid operations: BACKUP, RESTORE, DELETE");
			return false;
		}

		// <file_name>
		this.fileName = args[4];

		// <replication_degree>
		if(operation == Constants.Operation.BACKUP) {
			if(args.length < 6) {
				System.out.println("ERROR! Backup operation requires extra replication degree argument.");
				return false;
			}

			if (args[5].matches("[0-9]{1,5}")) {
				this.replicationDeg = Integer.parseInt(args[5]);
			} else {
				System.out.println("ERROR! Invalid replication degree argument.");
				return false;
			}
		}
		return true;
	}

	private boolean getPeerRMIStub() {
		try {
            this.stub = (RMIInterface) Naming.lookup(peerAccessPoint);
            
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.out.println("ERROR! Couldn't access RMI object.");
            return false;
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("ERROR! Invalid RMI access point.");
            return false;           
        }

		return true;
	}

	private void calculateClientId() {
		this.id = ip + "." + port;
	}

	private void printInfo() {
		System.out.println("Client id: " + this.id);
		System.out.println("IP address: " + this.ip);
		System.out.println("port: " + this.port);
		System.out.println("Peer RMI access point: " + this.peerAccessPoint);
		System.out.println("operation: " + this.operation.name());
		if(operation == Constants.Operation.BACKUP) {
			System.out.println("File path: " + this.fileName);
			System.out.println("Replication degree: " + this.replicationDeg);
		} else {
			System.out.println("File name: " + this.fileName);
		}
	}

	private boolean performStubOperation() throws IOException {		
		int response = 0;
		
		switch (operation) {
		case BACKUP:
			PartitionedFile backupFile;
			try {
				backupFile = new PartitionedFile(this.id, this.fileName, Constants.FileType.BACKEDUP);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
	            System.out.println("Invalid file path");
	            return false;
			}
			if(backupFile.uploadToEndpoint(stub)) {
				response = stub.backup(this.id, backupFile.getFileName(), this.replicationDeg);
			}
			break;
			
		case RESTORE:
			PartitionedFile restoreFile = new PartitionedFile(this.id, this.fileName, Constants.FileType.RESTORED);
			if((response = stub.restore(this.id, this.fileName)) == Constants.SUCCESS) {
				restoreFile.downloadFromEndpoint(stub);
			}
			break;
			
		case DELETE:
			response = stub.delete(this.id, this.fileName);
			break;
    	}
		
		switch(response) {
		case Constants.SUCCESS:
			System.out.println("Operation successful!");
			break;
			
		case Constants.FILE_CHUNKS_NOT_RECEIVED:
			System.out.println("ERROR! File has been incorrectly uploaded to peer");
			break;
			
		case Constants.FILE_NOT_BACKEDUP:
			System.out.println("ERROR! File was not backedup into the system");
			break;
		}
		
		return true;
	}

	private String getPublicIP() throws IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		return in.readLine();
	}
}
