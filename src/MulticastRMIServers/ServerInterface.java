package MulticastRMIServers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ServerInterface extends Remote {
    HashMap<String, Object> register(String username, String password) throws RemoteException;

    HashMap<String, Object> login(String username, String password) throws RemoteException;

    HashMap<String, Object> getSearchHistory(String username) throws RemoteException;

    HashMap<String, Object> search(String searchTarget, String username) throws RemoteException;

    HashMap<String, Object> connectedPages(String pageURL) throws RemoteException;

    HashMap<String, Object> indexNewURL(String newURL, int level) throws RemoteException;

    HashMap<String, Object> getNotifications(String username) throws RemoteException;

    HashMap<String, Object> getAdministrationPage() throws RemoteException;

    HashMap<String, Object> grantPrivileges(String targetUser) throws RemoteException;

    void addMe(String username, CallBack client) throws RemoteException;
}
