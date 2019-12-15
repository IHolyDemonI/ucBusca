package Models;

import java.rmi.RemoteException;
import java.util.HashMap;

public class NotificationBean extends Bean{
    private String username;

    public NotificationBean(){
        super();
    }

    public HashMap<String, Object> getNotifications(){
        try {
            return rmiServer.getNotifications(username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
