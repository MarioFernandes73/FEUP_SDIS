package rmi;

import peer.Peer;
import utils.Constants;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMICreator {

    private int port;
    private boolean communicationReady = false;

    public RMICreator(Peer obj, String accessPoint) {

        extractRMIPort(accessPoint);
        System.out.println("RMI access point: " + accessPoint);
        System.out.println("RMI port: " + this.port);

       /* try {
            LocateRegistry.getRegistry(this.port);
            System.out.println("Registry up on port " + this.port);
        } catch (RemoteException e) {
            try {
                LocateRegistry.createRegistry(this.port);
                System.out.println("Registry created on port " + this.port);
            } catch (RemoteException e1) {
                e1.printStackTrace();
                System.out.println("Couldn't access to rmi registry on specified port (" + this.port + ")");
                return;
            }
        }*/

        try {
            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(obj, 0);
            // Bind remote object to rmi registry
            Naming.rebind(accessPoint, stub);
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

    private void extractRMIPort(String accessPoint) {
        int colonPosition = accessPoint.lastIndexOf(':');
        if(colonPosition < 4) { // The number is 4 to ignore the colon in the specific case "rmi://..."
            this.port = Constants.RMI_DEFAULT_PORT;
            return;
        }

        int lastBarPosition = accessPoint.lastIndexOf('/');
        if(lastBarPosition <= colonPosition) {
            this.port = Constants.RMI_DEFAULT_PORT;
            return;
        }

        try {
            this.port = Integer.parseInt(accessPoint.substring(colonPosition + 1, lastBarPosition));
        } catch (NumberFormatException e) {
            this.port = Constants.RMI_DEFAULT_PORT;
            System.out.println("Invalid RMI port in access point: " + accessPoint);
            System.out.println("Default port " + Constants.RMI_DEFAULT_PORT + " will be used");
            return;
        }
    }
}