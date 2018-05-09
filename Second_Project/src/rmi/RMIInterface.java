package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {
	int transferFileChunk(String fileName, int chunkNo, byte[] chunkContent) throws RemoteException;
	byte[] getFileChunk(String fileName, int chunkNo) throws RemoteException;
	int backup(String fileName, int replicationDegree) throws RemoteException;
	int restore(String fileName) throws RemoteException;
	int delete(String fileName) throws RemoteException;
	String state() throws RemoteException;
}
