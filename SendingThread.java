import java.io.IOException;
import java.net.DatagramSocket;
import java.net.*;

/**
 * SendingThread.java
 *
 * @author Rasika Thorat
 */
public class SendingThread implements Runnable{

    RoutingTable rt;
    SendingThread(RoutingTable rt){
        this.rt = rt;
    }

    /**
     * Function to send the routing tables to the neighbors of the router.
     * The message is sent in a particular string format so that the receiver an decode.
     *
     * @throws IOException
     */
    void broadcastToNeighbors() throws IOException {

        //UDP socket connection
        DatagramSocket senderSocket = new DatagramSocket();

        // Routing table details
        while(true) {
            byte sendTable[] = null;

            StringBuilder message = new StringBuilder(rt.routerName +";");//rt.routerName + " " + rt.routerIp);


            for(String key: rt.routingTable.keySet()){
                message.append(key + " " + rt.routingTable.get(key).get("Destination") + " "
                        + rt.routingTable.get(key).get("Next Hop") + " " + rt.routingTable.get(key).get("Distance") + " "
                        + rt.routingTable.get(key).get("Port") + " " + rt.routingTable.get(key).get("Subnet") + ";");
            }

            //Sending to neighbors
            sendTable = message.toString().getBytes();
            String[] neighborIp = rt.getNeighborIp();
            int[] neighborPort = rt.getNeighborPort();

            for (int j = 0; j < rt.neighbors; j++) {
                InetAddress inet = InetAddress.getByName(neighborIp[j]);
                DatagramPacket dp = new DatagramPacket(sendTable, sendTable.length, inet, neighborPort[j]);
                senderSocket.send(dp);

            }
        }


    }

    @Override
    public void run() {
        try {
            broadcastToNeighbors();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
