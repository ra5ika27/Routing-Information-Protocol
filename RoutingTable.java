import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RoutingTable.java
 *
 * @author Rasika Thorat
 */

public class RoutingTable implements Serializable {

    ConcurrentHashMap<String, Long> lastReceived= new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> routingTable = new ConcurrentHashMap<>();
    String routerName;
    String routerIp;
    int routerReceivingPort;
    int neighbors;
    String[] neighbor_det;

    RoutingTable(String routerName, String routerIp, int routerReceivingPort,int neighbors, String[] neighbor_det) {
        this.routerName = routerName;
        this.routerIp = routerIp;
        this.routerReceivingPort = routerReceivingPort;
        this.neighbors = neighbors;
        this.neighbor_det=neighbor_det;
        for (int i = 0; i < neighbors; i++) {
            String[] neighbor_split = neighbor_det[i].split("\\s+");
            this.lastReceived.put(neighbor_split[0],0L);
        }
    }

    /**
     * Function to add the immediate neighbors in the hash map using the configuration files.
     *
     */
    void addIntial() {
        for (int i = 0; i < neighbors; i++) {
            String[] neighbor_split = neighbor_det[i].split("\\s+");
            ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
            map.put("Destination", neighbor_split[1]);
            map.put("Next Hop", neighbor_split[1]);
            map.put("Port", neighbor_split[2]);
            map.put("Distance", neighbor_split[3]);
            map.put("Subnet",neighbor_split[4]);
            routingTable.put(neighbor_split[0], map);
        }

    }

    /**
     * Function to get the IP addresses of the neighbors.
     *
     * @return neighbor ip address
     */
    String[] getNeighborIp(){

        String[] neighborIp = new String[2];
        for(int i=0;i<neighbors;i++) {
            String[] neighbor_split = neighbor_det[i].split("\\s+");
            neighborIp[i] = neighbor_split[1];
        }
        return neighborIp;
    }

    /**
     * Function to get the port numbers of the neighbors.
     *
     * @return neighbor port
     */
    int[] getNeighborPort(){
        int[] neighborPort = new int[2];
        for(int i=0;i<neighbors;i++) {
            String[] neighbor_split = neighbor_det[i].split("\\s+");
            neighborPort[i] = Integer.parseInt(neighbor_split[2]);
        }
        return neighborPort;
    }

    /**
     * Function to get the link cost from the router to another reachable router
     * given by senderName.
     *
     * @param senderName reachable router name
     * @return link cost
     */
    int getLinkCost(String senderName){
        for(int i=0;i<neighbors;i++) {
            String[] neighbor_split = neighbor_det[i].split("\\s+");
            if(neighbor_split[0].equals(senderName)){
                return Integer.parseInt(neighbor_split[3]);
            }
        }
        return 999;
    }

    /**
     * Function to add a new router in the routing table by taking the ip, next hop, distance to
     * the new router port and the subnet mask of the new router.
     *
     * @param destName reachable router name
     * @param ip Ip address
     * @param nextHop next hop router
     * @param port port number
     * @param newCost cost
     * @param subnet subnet mask
     */
    void addNewDest(String destName,String ip,String nextHop,String port,int newCost,String subnet){
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("Destination", ip);
        map.put("Next Hop", nextHop);
        map.put("Port", port);
        map.put("Distance", newCost);
        map.put("Subnet",subnet);

        routingTable.put(destName,map);
    }

    /**
     * Function to return the IP address in CIDR notations by taking the IP and the
     * subnet mask as the input.
     *
     * @param ipAddress ip address of the neighbors
     * @param subnet subnet mask
     * @return
     */
    String ipToCIDR(String ipAddress, String subnet){
        int mask = 0;
        String[] splitSubnet = subnet.split("\\.");
        for(String part: splitSubnet){
            if(!part.equals("0")) {
                int temp = Integer.parseInt(part);
                mask += Integer.toBinaryString(temp).length();
            }
        }

        return ipAddress+"/"+mask;

    }

    /**
     * Function to display the routing table by accessing the values in the hash map.
     * The function is called in the print table class.
     */
    void display() {

        System.out.println("-----------------------------------------------------------");
        System.out.println("                      " + routerName + "         ");
        System.out.println("-----------------------------------------------------------");
        System.out.println("Destination\t\tNext Hop\t\tDistance");

        for (ConcurrentHashMap<String, Object> stringObjectHashMap : routingTable.values()) {
            System.out.println(ipToCIDR(stringObjectHashMap.get("Destination").toString(),
                    stringObjectHashMap.get("Subnet").toString())+"          "+ipToCIDR(stringObjectHashMap.get("Next Hop").toString(),
                    stringObjectHashMap.get("Subnet").toString())
                    +"          "+stringObjectHashMap.get("Distance"));
        }

    }

}
