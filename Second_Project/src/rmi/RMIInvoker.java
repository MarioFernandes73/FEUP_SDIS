package rmi;

import rmi.RMIInterface;
import java.rmi.Naming;

public class RMIInvoker {

    private RMIInterface stub;

    public RMIInvoker(String accessPoint, String operation) {
        try {
            this.stub = (RMIInterface) Naming.lookup(accessPoint);
            extractOperation();
            invokeMethod();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.out.println("Error accessing RMI object");
            return;
        } catch(MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid RMI access point");
            return;
        }
    }

    private extractOperation(String operation) {

    }

    private invokeMethod() {

    }
}