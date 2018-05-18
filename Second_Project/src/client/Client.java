package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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
		
		if (!performStubOperation()) {
			throw new Exception();
		}
    }

	private static boolean verifyArgs(String args[]) {
		return true;
	}
	
	private static boolean getPeerRMIStub() {
		try {
            stub = (RMIInterface) Naming.lookup(peerAccessPoint);
            
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.out.println("Error accessing RMI object");
            return false;
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid RMI access point");
            return false;           
        }
		
		return true;
	}
	
	private boolean performStubOperation() throws IOException {		
		int response = 0;
		
		switch (operation) {
		case BACKUP:
			PartitionedFile backupFile;
			try {
				backupFile = new PartitionedFile(fileName, Constants.FileType.BACKEDUP);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
	            System.out.println("Invalid file path");
	            return false;
			}
			if(backupFile.uploadToEndpoint(stub)) {
				response = stub.backup(fileName, replicationDeg);		
			}
			break;
			
		case RESTORE:
			PartitionedFile restoreFile = new PartitionedFile(fileName, Constants.FileType.RESTORED);				
			if((response = stub.restore(fileName)) == Constants.SUCCESS) {
				restoreFile.downloadFromEndpoint(stub);
			}
			break;
			
		case DELETE:
			response = stub.delete(fileName);
			break;
			
		case STATE:
			System.out.println(stub.state());
			return true;
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
}
