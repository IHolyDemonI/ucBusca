package Models;

import java.rmi.RemoteException;
import java.util.HashMap;

public class SearchBean extends Bean{
    private String searchTarget;
    private String username = null;

    public SearchBean(){
        super();
    }

    public HashMap<String, Object> search(){
        try {
            return rmiServer.search(searchTarget, username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSearchTarget() {
        return searchTarget;
    }

    public void setSearchTarget(String searchTarget) {
        this.searchTarget = searchTarget;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
