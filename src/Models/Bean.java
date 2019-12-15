package Models;

import MulticastRMIServers.ServerInterface;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Bean implements Serializable {
    ServerInterface rmiServer = null;

    public Bean (){
        do {
            String RMIServerIP1 = "localhost";
            int RMIServerPort = 1098;

            try {
                rmiServer = (ServerInterface) LocateRegistry.getRegistry(RMIServerIP1, RMIServerPort).lookup("Server");
                //rmiServer = (ServerInterface) Naming.lookup("Server");
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
                rmiServer = null;
            }
        }while (rmiServer == null);
    }
}
