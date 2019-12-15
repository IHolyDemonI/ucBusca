package MulticastRMIServers;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class RMIServer extends UnicastRemoteObject implements ServerInterface, Runnable{
    Property properties;

    private String multicastHost; //ip da
    private String multicastAdress;
    private int rmiMulticastPort;
    private int bufferSize;
    private int multicastPort;
    private int rmiPort;

    private HashMap<String, CallBack> clients = new HashMap<>();

    RMIServer() throws RemoteException {
        properties = new Property("UcBusca.properties");

        multicastHost = properties.getProperty("interfaceIP");
        multicastAdress = properties.getProperty("MulticastAddress");
        rmiMulticastPort = Integer.parseInt(properties.getProperty("RMImulticastPort"));
        bufferSize = Integer.parseInt(properties.getProperty("BufferSize"));
        multicastPort = Integer.parseInt(properties.getProperty("MulticastPort"));
        rmiPort = Integer.parseInt(properties.getProperty("RMIServerPort"));

        // ========== START RMI SERVER ==========
        System.setProperty("java.security.policy", "file:./ ");

        LocateRegistry.createRegistry(rmiPort).rebind("Server", this);
        System.out.println("Server ready...");


        // ========== TimerTask for updating properties ==========
        new Timer().scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run(){
                //System.out.println("Updating properties");
                multicastHost = properties.getProperty("interfaceIP");
                multicastAdress = properties.getProperty("MulticastAddress");
                rmiMulticastPort = Integer.parseInt(properties.getProperty("RMImulticastPort"));
                bufferSize = Integer.parseInt(properties.getProperty("BufferSize"));
                multicastPort = Integer.parseInt(properties.getProperty("MulticastPort"));
            }
        }, 30000, 20000);
    }

    public static void main(String[] args) throws RemoteException {
        new RMIServer();
    }

    @Override
    public void run() {

    }


    // ========== RMI Methods ==========

    public HashMap<String, Object> register(String username, String password) {
        HashMap<String, Object> hashMap = ProtocolMessage.registryRequest(username, password);

        hashMap = multiCastCommunication(hashMap, true);

        return hashMap;
    }

    public HashMap<String, Object> login(String username, String password) {
        HashMap<String, Object> hashMap = ProtocolMessage.loginRequest(username, password);

        hashMap = multiCastCommunication(hashMap, true);

        return hashMap;
    }

    public HashMap<String, Object> search(String searchTarget, String username) {
        HashMap<String, Object> hashMap = ProtocolMessage.searchRequest(searchTarget, username);

        hashMap = multiCastCommunication(hashMap, true);

        return hashMap;
    }

    public HashMap<String, Object> getSearchHistory(String username) {
        HashMap<String, Object> hashMap = ProtocolMessage.searchHistoryRequest(username);

        hashMap = multiCastCommunication(hashMap, true);

        return hashMap;
    }

    public HashMap<String, Object> connectedPages(String pageURL) {
        HashMap<String, Object> hashMap = ProtocolMessage.connectedPagesRequest(pageURL);

        hashMap = multiCastCommunication(hashMap, true);

        return hashMap;
    }

    public HashMap<String, Object> indexNewURL(String newURL, int level){
        HashMap<String, Object> hashMap = ProtocolMessage.indexNewURLRequest(newURL, level);

        hashMap = multiCastCommunication(hashMap, false);

        return hashMap;
    }

    public HashMap<String, Object> getNotifications(String username) {
        HashMap<String, Object> hashMap = ProtocolMessage.getNotificationsRequest(username);

        hashMap = multiCastCommunication(hashMap, true);

        return hashMap;
    }

    public HashMap<String, Object> getAdministrationPage(){
        HashMap<String, Object> hashMap = ProtocolMessage.getAdministrationPageRequest();

        hashMap = multiCastCommunication(hashMap, true);

        return hashMap;
    }

    public HashMap<String, Object> grantPrivileges(String targetUser){
        HashMap<String, Object> hashMap = ProtocolMessage.grantPrivilegesRequest(targetUser);

        hashMap = multiCastCommunication(hashMap, true);

        if (hashMap.containsKey("TargetUser")){
            if (clients.containsKey(hashMap.get("TargetUser"))){
                try {
                    clients.get(hashMap.get("TargetUser")).setNewNotificationsTrue();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        return hashMap;
    }

    public void addMe(String username, CallBack client){
        clients.put(username, client);
    }

    private HashMap<String, Object> multiCastCommunication(HashMap<String, Object> request, boolean needAnswer){
        HashMap<String, Object> answer = new HashMap<>();

        MulticastSocket multicastSocket = null;
        InetAddress multicastGroup;
        byte[] buffer;
        DatagramPacket packet;

        int multicastID = 1;

        try {
            multicastSocket = new MulticastSocket(rmiMulticastPort);
            multicastGroup = InetAddress.getByName(multicastAdress);
            multicastSocket.joinGroup(multicastGroup);

            //add id of the RMI server
            request.put("ServerID", multicastID);

            //send to multicast
            buffer = ProtocolMessage.hashMapToBytes(request);
            packet = new DatagramPacket(buffer, buffer.length, multicastGroup, multicastPort);
            multicastSocket.send(packet);

            while (true) {
                if (!needAnswer)
                    break;

                buffer = new byte[bufferSize];
                packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);
                buffer = packet.getData();

                answer = ProtocolMessage.bytesToHashMap(buffer);

                if ((int) answer.get("ServerID") == 0){
                    if (answer.get("Type").equals("administrationPageAnswer")){
                        ArrayList <String> multicastIPs = new ArrayList<>();
                        ArrayList <Integer> multicastPorts = new ArrayList<>();

                        multicastIPs.add(packet.getAddress().getHostAddress());
                        multicastPorts.add(packet.getPort());

                        answer.put("MuticastIPs", multicastIPs);
                        answer.put("MulticastPorts", multicastPorts);
                    }

                    return answer;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            multicastSocket.close();
        }
        return answer;
    }

    /*
    private HashMap<String, Object> multiCastCommunication(HashMap<String, Object> map, boolean needAnswer) {
        while (true) {
            try {
                InetAddress multicastGroup = InetAddress.getByName(multicastHost);

                map.put("ServerID", 1);

                //HashMaps to bytes
                byte[] buffer = ProtocolMessage.hashMapToBytes(map);

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multicastGroup, 4444);
                clientMulticastSocket.send(packet);

                clientMulticastSocket = new MulticastSocket(4444);
                clientMulticastSocket.joinGroup(multicastGroup);
                //Espera pela resposta do servidor multicast para finalizar a ação
                while (true) {
                    buffer = new byte[16767];
                    packet = new DatagramPacket(buffer, buffer.length);
                    clientMulticastSocket.receive(packet);
                    buffer = packet.getData();

                    //Bytes to HashMaps
                    HashMap<String, Object> answer = ProtocolMessage.bytesToHashMap(buffer);
                    clientMulticastSocket.close();
                    return answer;
                }

            } catch (Exception e) {
                System.out.println("Server seems to be offline");
            }
        }
    }
    */
}
