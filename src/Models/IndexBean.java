package Models;

import java.rmi.RemoteException;
import java.util.HashMap;

public class IndexBean extends Bean{
    private String newURL;
    private int level;

    public IndexBean(){
        super();
    }

    public HashMap<String, Object> indexNewURL(){
        try {
            return rmiServer.indexNewURL(newURL, level);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNewURL() {
        return newURL;
    }

    public void setNewURL(String newURL) {
        this.newURL = newURL;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
