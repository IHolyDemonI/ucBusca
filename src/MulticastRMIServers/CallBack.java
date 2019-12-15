package MulticastRMIServers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallBack extends Remote{
    void setNewNotificationsTrue() throws RemoteException;
}
