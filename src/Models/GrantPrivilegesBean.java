package Models;

import java.rmi.RemoteException;
import java.util.HashMap;

public class GrantPrivilegesBean extends Bean{
    private String targetUser;

    public GrantPrivilegesBean(){
        super();
    }

    public HashMap<String, Object> grantPrivileges(){
        try {
            return rmiServer.grantPrivileges(targetUser);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }
}
