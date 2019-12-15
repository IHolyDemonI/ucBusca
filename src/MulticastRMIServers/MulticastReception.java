package MulticastRMIServers;

import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.HashMap;

public class MulticastReception extends Thread{
    private int multicastPort;
    private String multicastAddress;
    private int multicastServerID;

    private int bufferSize;

    private MulticastServer server;

    /**
     * Only used to be the thread that receives requests from RMIServer and stores them in
     * {@link MulticastServer#requestQueue}
     * @param server reference to the MulticastServer class. Needed to access function
     * {@link MulticastServer#addRemoveRequest(HashMap, boolean)}
     * @param multicastServerID used to check if the request is to be processed my this server
     * @param multicastPort port where the server is supposed to be receiving requests
     * @param multicastAddress multicast group address
     * @param bufferSize speaks for it self doesn't it?
     */
    MulticastReception (MulticastServer server, int multicastServerID, int multicastPort, String multicastAddress, int bufferSize){
        this.multicastPort = multicastPort;
        this.multicastAddress = multicastAddress;
        this.bufferSize = bufferSize;
        this.server = server;
        this.multicastServerID = multicastServerID;
    }

    @Override
    public void run() {
        MulticastSocket multicastSocket = null;
        InetAddress multicastGroup;

        byte[] buffer;
        DatagramPacket packet;

        HashMap<String, Object> request;

        try {
            //connect to multicast group
            multicastSocket = new MulticastSocket(multicastPort);
            multicastGroup = InetAddress.getByName(multicastAddress);
            multicastSocket.joinGroup(multicastGroup);


            while (true){
                //receive message
                buffer = new byte[bufferSize];
                packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);
                buffer = packet.getData();

                //transform to HashMap
                request = ProtocolMessage.bytesToHashMap(buffer);

                //skip if not for me
                if (!request.get("ServerID").equals(multicastServerID))
                    continue;

                //add to queue
                if (request.get("Type").equals("indexNewURLRequest"))
                    server.addRemoveRequest(request, true);
                else
                    server.addRemoveRequest(request, false);
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (multicastSocket != null)
                multicastSocket.close();
        }
    }
}
