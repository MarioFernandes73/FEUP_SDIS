package communications;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {
	
	String backup(String fileName, int replicationDegree, boolean enhancement) throws RemoteException;
	String restore(String fileName, boolean enhancement) throws RemoteException;
	String delete(String fileName, boolean enhancement) throws RemoteException;
	String reclaim(int space, boolean enhancement) throws RemoteException;
	String state() throws RemoteException;
}
