package rmi;

import rmi.RMIInterface;
import utils.Constants;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMICreator {

    private boolean communicationReady = false;

    public RMICreator(RMIInterface obj, String accessPoint) {
        URL rmiUrl = null;
        int port;
        try {
            rmiUrl = new URL(accessPoint);
            port = (rmiUrl.getPort() == -1) ? Constants.RMI_DEFAULT_PORT : rmiUrl.getPort();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid access point URL");
            return;
        }

        try {
            LocateRegistry.getRegistry(port);
        } catch (RemoteException e) {
            try {
                LocateRegistry.createRegistry(port);
            } catch (RemoteException e1) {
                e1.printStackTrace();
                System.out.println("Couldn't access to rmi registry on specified port (" + port + ")");
                return;
            }
        }

        try {
            // Bind remote object to rmi registry
            Naming.rebind(accessPoint, obj);
            communicationReady = true;
            System.out.println("Server ready for RMI communication " + "\n \tAccess point: " + accessPoint);

        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("RMI stub creation error");
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid access point");
            return;
        }
    }

    public boolean isCommunicationReady() {
        return this.communicationReady;
    }
}