package rmi;

import rmi.RMIInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class RMICreator {

    public RMICreator(RMIInterface obj, String accessPoint) {
        try {
            // Bind remote object to rmi registry
            Naming.rebind(accessPoint, obj);

            System.out.println("Server ready for RMI communication " + "\n \tAccess point: " + accessPoint);

        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("RMI stub creation error");

        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid access point");

        }
    }
}