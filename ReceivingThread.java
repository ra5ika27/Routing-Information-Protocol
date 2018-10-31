import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * ReceivingThread.java
 *
 * @author Rasika Thorat
 *
 */

public class ReceivingThread implements Runnable{

    RoutingTable rt;
    ReceivingThread(RoutingTable rt){
        this.rt = rt;
    }

    /**
     * Function to receive the routing table from the neighbors. Store the current time
     * every time a neighbor sends their routing table. Check if the neighbor is dead by
     * checking the last heard from the neighbor. If it is higher than a predefined value
     * then the neighbor is declared isolated. Triggered update function is called.
     * If there is an update in the table the the min cost is calculated and the router's
     * routing table is updated.
     *
     * @throws IOException
     */
    void receiveFromNeighbors() throws IOException {

        DatagramSocket receiverSocket = new DatagramSocket(rt.routerReceivingPort);


        while(true) {

            byte[] receivedDataBytes = new byte[65535];

            DatagramPacket dp = new DatagramPacket(receivedDataBytes, receivedDataBytes.length);
            receiverSocket.receive(dp);

            //Convert bytes to string
            StringBuilder sb = new StringBuilder();
            int inc = 0;
            while(receivedDataBytes[inc] !=0){
                sb.append((char)receivedDataBytes[inc]);
                inc ++;
            }
            String receivedData = sb.toString();
            String[] splitReceivedData = receivedData.split(";");
            String senderName = splitReceivedData[0];

            //update time for the sender
            long receivedTime = System.currentTimeMillis();
            rt.lastReceived.put(senderName,receivedTime);

            boolean isIsolated = false;
            String isolatedNode = "";
            //check if other senders are isolated
            for(String key: rt.lastReceived.keySet()) {
                if (!rt.lastReceived.get(key).equals(0L) && !key.equals(senderName)) {
                    long lastReceived = rt.lastReceived.get(key);
                    if ((receivedTime - lastReceived) > 5000) {
 //                       System.out.println("Router " + key + " isolated");
                        isolatedNode = key;
                        isIsolated = true;
                    }
                }
            }


            if(isIsolated){
                rt.routingTable.get(isolatedNode).put("Distance",999);
                for(String key: rt.routingTable.keySet()){
                    if(rt.routingTable.get(key).get("Next Hop").equals(rt.routingTable.get(isolatedNode).get("Destination"))){
                        rt.routingTable.get(key).put("Distance",999);
                    }
                }
            }

            String senderIp = rt.routingTable.get(senderName).get("Destination").toString();

            for (int val=1;val<splitReceivedData.length;val++) {
                String[] destDet = splitReceivedData[val].split("\\s+");
                if(destDet.length == 6) {
                    int linkCost = rt.getLinkCost(senderName);

                    //check if destination IP in routing table
                    if (rt.routingTable.containsKey(destDet[0])) {

                        if (destDet[2].equals(rt.routerIp)) {
                            linkCost = 999;
                        }
                        int newCost = linkCost + Integer.parseInt(destDet[3]);
                        if(Integer.parseInt(destDet[3]) == 999 && rt.routingTable.get(destDet[0]).get("Next Hop").equals(senderIp)){

                            rt.routingTable.get(destDet[0]).put("Distance", 999);
                        }

                        if (newCost < Integer.parseInt(rt.routingTable.get(destDet[0]).get("Distance").toString())) {
                            rt.routingTable.get(destDet[0]).put("Distance", Integer.toString(newCost));
                            String nextHop = senderIp;
                            rt.routingTable.get(destDet[0]).put("Next Hop", nextHop);
                        }
                        for (int i = 0; i < rt.neighbors; i++) {
                            String[] neighbor_split = rt.neighbor_det[i].split("\\s+");
                            if (!neighbor_split[0].equals(isolatedNode) && Integer.parseInt(
                                    rt.routingTable.get(neighbor_split[0]).get("Distance").toString()) == 999) {
                                rt.routingTable.get(neighbor_split[0]).put("Distance", neighbor_split[3]);
                                rt.routingTable.get(neighbor_split[0]).put("Next Hop", neighbor_split[1]);
                            }
                        }


                    } //check if destination Ip is not same as receiver IP
                    else if (!rt.routerName.equals(destDet[0])) {
                        int newCost = linkCost + Integer.parseInt(destDet[3]);
                        String nextHop = senderIp;
                        String port = destDet[4];
                        String subnet = destDet[5];
                        String ip = destDet[1];
                        String destName = destDet[0];
                        rt.addNewDest(destName, ip, nextHop, port, newCost, subnet);

                    }
                }

            }

        }
    }


    @Override
    public void run() {
        try {
            receiveFromNeighbors();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
