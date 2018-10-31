import java.io.*;

/**
 * Router.java
 *
 * @author Rasika Thorat
 *
 */

public class Router {

    /**
     * Function to split the content of the file according to the format of the
     * content. Pass the configuration to the Routing Table class using the
     * Routing Table object.
     *
     * @param fileContent contents of the configuration file
     * @return Routing Table object
     */
    private static RoutingTable splitting(StringBuilder fileContent) {
        String[] config = fileContent.toString().split("\\s+");

        String routerName = config[0];
        String routerIp = config[1];
        int routerReceivingPort = Integer.parseInt(config[2]);
        int neighbors = Integer.parseInt(config[3]);
        String[] neighbor_det = new String[2];
        int j =0;
        for(int i=0;i<neighbors;i++){
            neighbor_det[i] = config[j+4] + " " + config[j+5] + " " + config[j+6] + " "+config[j+7]+" "+config[j+8];
            j=j+5;
        }

        RoutingTable rt = new RoutingTable(routerName,routerIp,routerReceivingPort,neighbors,neighbor_det);
        rt.addIntial();
        return rt;

    }

    /**
     * Function to take a configuration file as an input and pass the
     * file contents to a function to split the data. Also, to call the
     * sending, receiving and print table threads to start the router.
     *
     * @param args command-line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        try {

            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BufferedReader br1 = null;
        StringBuilder fileContent = new StringBuilder();

        try {
            br1 = new BufferedReader(new FileReader(new File(args[0])));
            String s;
            while((s = br1.readLine()) != null){
                fileContent.append(s+" ");
            }

        }catch (FileNotFoundException f){
            System.out.println("File not found");
        }

        //receive the router table object to send to thread
        RoutingTable rt = splitting(fileContent);

        //starting a new sender,receiver and printTable thread
        Thread send = new Thread(new SendingThread(rt));
        Thread receive = new Thread(new ReceivingThread(rt));
        Thread printTable = new Thread(new PrintTable(rt));
        send.start();
        receive.start();
        printTable.start();

    }
}
