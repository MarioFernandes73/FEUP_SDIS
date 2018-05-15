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

public class Main {
	
	private static String peerAccessPoint;
	private static String selfAccessPoint;
	private static Constants.Operation operation;
	private static String fileName;
	private static int replicationDeg;
	private static RMIInterface stub;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(!verifyArgs(args))
    		return;
		
		if(!getPeerRMIStub())
    		return;
		try{
			performStubOperation();
		} catch(Exception e){
		    e.printStackTrace();
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
	
	private static void performStubOperation() throws IOException {
		int response = 0;
		switch (operation) {
		case BACKUP:
			PartitionedFile backupFile;
			try {
				backupFile = new PartitionedFile(fileName, Constants.FileType.BACKEDUP);					
			} catch (FileNotFoundException e) {
				e.printStackTrace();
	            System.out.println("Invalid file path");
	            return;
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
			return;
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
	}
}
