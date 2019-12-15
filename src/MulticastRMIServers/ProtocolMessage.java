package MulticastRMIServers;

import java.io.*;
import java.util.HashMap;

public class ProtocolMessage {

    public static HashMap<String, Object> requestHeartbeat (String rmiID){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "heartbeat");
        requestMap.put("Origin", rmiID);
        requestMap.put("Receiver", "");

        return requestMap;
    }

    public static HashMap<String, Object> registryRequest(String username, String password){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "register");
        requestMap.put("Username", username);
        requestMap.put("Password", password);

        return requestMap;
    }

    public static HashMap<String, Object> loginRequest(String username, String password){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "loginRequest");
        requestMap.put("Username", username);
        requestMap.put("Password", password);

        return requestMap;
    }

    public static HashMap<String, Object> searchHistoryRequest(String username){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "searchHistoryRequest");
        requestMap.put("Username", username);

        return requestMap;
    }

    public static HashMap<String, Object> searchRequest(String searchTarget, String username){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "searchRequest");
        requestMap.put("SearchTarget", searchTarget);
        requestMap.put("Username", username);

        return requestMap;
    }

    public static HashMap<String, Object> connectedPagesRequest(String pageURL){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "connectedPagesRequest");
        requestMap.put("PageURL", pageURL);

        return requestMap;
    }

    public static HashMap<String, Object> indexNewURLRequest(String newURL, int level){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "indexNewURLRequest");
        requestMap.put("Level", level);
        requestMap.put("URL", newURL);

        return requestMap;
    }

    public static HashMap<String, Object> getNotificationsRequest(String username){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "getNotificationsRequest");
        requestMap.put("Username", username);

        return requestMap;
    }

    public static HashMap<String, Object> getAdministrationPageRequest(){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "getAdministrationPageRequest");

        return requestMap;
    }

    public static HashMap<String, Object> grantPrivilegesRequest(String targetUser){
        HashMap<String, Object> requestMap = new HashMap<>();

        requestMap.put("Type", "grantPrivilegesRequest");
        requestMap.put("TargetUser", targetUser);

        return requestMap;
    }

    /**
     *
     * @param map ?
     * @return conversao para bytes
     * @throws IOException ?
     */
    public static byte[] hashMapToBytes(HashMap map) throws IOException {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(map);

        return byteOut.toByteArray();
    }

    /**
     *
     * @param message?
     * @return conversao para HashMaps
     * @throws Exception ?
     */
    public static HashMap<String, Object> bytesToHashMap(byte[] message) throws IOException, ClassNotFoundException {

        ByteArrayInputStream byteIn = new ByteArrayInputStream(message);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        //noinspection unchecked
        return (HashMap<String, Object>)in.readObject();

    }

}
