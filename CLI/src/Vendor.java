import java.util.Random;

public class Vendor extends Thread {
    private final String vendorName;
    private final TicketPool ticketPool;

    public Vendor(String vendorName, TicketPool ticketPool) {
        this.vendorName = vendorName;
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        while (!ticketPool.isSystemComplete()) {
            try {
                // The logic for ticket addition is handled in TicketPool
                ticketPool.addTickets(vendorName);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return; // Exit the thread if interrupted
            }
        }
    }
}