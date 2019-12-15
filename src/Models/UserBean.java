package Models;

import java.rmi.RemoteException;
import java.util.HashMap;

public class UserBean extends Bean {
    private String username;
    private String password;

    public UserBean(){
        super();
    }

    public HashMap<String, Object> tryLogin(){
        try {
            return rmiServer.login(username, password);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, Object> tryRegister(){
        try {
            return rmiServer.register(username, password);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
