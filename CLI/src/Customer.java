import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrievalRate;
    private final String customerId;
    private final Random random;
    private final CountDownLatch latch;

    public Customer(TicketPool ticketPool, int retrievalRate, String customerId, CountDownLatch latch) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
        this.customerId = customerId;
        this.random = new Random();
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            latch.await(); // Wait for the signal to start

            while (true) {
                if (ticketPool.isSystemComplete()) {
                    return;
                }

                int ticketsToRetrieve = random.nextInt(5) + 1; // Attempt to buy 1 to 5 tickets

                if (!ticketPool.removeTickets(ticketsToRetrieve, customerId) && ticketPool.getCurrentPool() > 0) {
                    System.out.println("Customer " + customerId + " wanted " + ticketsToRetrieve + " tickets. Not enough in the pool.");
                }

                Thread.sleep(retrievalRate); // Simulate the delay for retrieving tickets
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}