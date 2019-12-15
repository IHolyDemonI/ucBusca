package Models;

import MulticastRMIServers.ServerInterface;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

public class ConnectedURLBean extends Bean {

    public ConnectedURLBean(){
        super();
    }

    public HashMap<String, Object> fetchConnectedURLs(String target){
        try {
            return rmiServer.connectedPages(target);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
