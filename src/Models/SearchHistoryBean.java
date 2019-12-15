package Models;

import java.rmi.RemoteException;
import java.util.HashMap;

public class SearchHistoryBean extends Bean {

    public SearchHistoryBean(){
        super();
    }

    public HashMap<String, Object> fetchMySearchHistory(String username){
        try {
            return rmiServer.getSearchHistory(username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

}
