/**
 * PrintTable.java
 *
 * @author Rasika Thorat
 */
public class PrintTable implements Runnable{

    RoutingTable rt;

    /**
     * Default constructor to initialize the routing table object
     *
     * @param rt RoutingTable class object
     */
    PrintTable(RoutingTable rt){
        this.rt = rt;
    }

    /**
     * Function to call the display function of the Routing table.
     */
    void printRoutingTable(){
        rt.display();
    }

    @Override
    public void run() {
        while(true) {
            printRoutingTable();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
