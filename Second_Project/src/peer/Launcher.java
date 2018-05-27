package peer;


import rmi.RMIInterface;
import peer.Peer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Launcher {

    public static void main(String[] args)  {
/*
        String[] temp = new String[]{"PUTCHUNK", "senderId" };
        new MessageBuilder().build(temp);
*/
        Peer peer = null;
        try{
            peer = new Peer(args);
        } catch ( Exception e){
            e.printStackTrace();
            return;
        }

        try {
            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(peer, 0);
            System.out.println("Passou");
            // Bind remote object to rmi registry
            Naming.rebind(peer.getAccessPoint(), stub);
            System.out.println("Server ready for RMI communication " + "\n \tAccess point: " + peer.getAccessPoint());

        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("RMI stub creation error");
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid access point");
            return;
        }

        while(true) {

        }
    }

}
