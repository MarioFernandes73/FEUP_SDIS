package communications;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {
	
	String backup(String fileName, int replicationDegree, boolean enhancement) throws RemoteException;

}
