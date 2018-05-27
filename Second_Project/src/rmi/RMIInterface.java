package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {
	int transferFileChunk(String clientId, String fileName, int chunkNo, byte[] chunkContent) throws RemoteException;
	byte[] getFileChunk(String clientId, String fileName, int chunkNo) throws RemoteException;
	int backup(String clientId, String fileName, int replicationDegree) throws RemoteException;
	int restore(String clientId, String fileName) throws RemoteException;
	int delete(String clientId, String fileName) throws RemoteException;
}
