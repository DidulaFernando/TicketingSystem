import java.util.Random;

public class TicketPool {
    private final int maxPool;
    private int currentPool;
    private int remainingTickets;
    private boolean salesComplete;
    private boolean addingComplete;
    private final Random random;

    public TicketPool(int maxPool, int totalTickets) {
        this.maxPool = maxPool;
        this.currentPool = 0;
        this.remainingTickets = totalTickets;
        this.salesComplete = false;
        this.addingComplete = false;
        this.random = new Random();
    }

    public synchronized boolean addTickets(String vendorName) {
        if (addingComplete) {
            return false;
        }

        int ticketsToAdd = random.nextInt(5) + 1;  // Random from 1 to 5
        ticketsToAdd = Math.min(ticketsToAdd, remainingTickets); // Adjust if more than remaining

        while (currentPool + ticketsToAdd > maxPool) {
            if (addingComplete) {
                return false;
            }
            try {
                System.out.println("Vendor " + vendorName + " waiting to add tickets. Pool is full.");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        // Re-calculate ticketsToAdd just in case the conditions have changed after waiting
        ticketsToAdd = Math.min(ticketsToAdd, remainingTickets);

        currentPool += ticketsToAdd;
        remainingTickets -= ticketsToAdd;

        if (remainingTickets <= 0) {
            remainingTickets = 0;
            addingComplete = true;
        }

        System.out.println("Vendor " + vendorName + " added " + ticketsToAdd + " tickets. Current pool: " + currentPool + ". Remaining tickets to release: " + remainingTickets);

        notifyAll();
        return true;
    }

    public synchronized boolean removeTickets(int tickets, String customerName) {
        if (currentPool < tickets) {
            System.out.println("Customer " + customerName + " wanted " + tickets + " tickets. Not enough in the pool.");
            return false;
        }

        currentPool -= tickets;
        System.out.println("Customer " + customerName + " bought " + tickets + " tickets. Remaining pool: " + currentPool);

        if (remainingTickets <= 0 && currentPool == 0) {
            salesComplete = true;
        }

        notifyAll();
        return true;
    }

    public synchronized int getCurrentPool() {
        return currentPool;
    }

    public synchronized boolean isAddingComplete() {
        return addingComplete;
    }

    public synchronized boolean isSystemComplete() {
        return salesComplete;
    }

    public synchronized int getRemainingTicketsToRelease() {
        return remainingTickets;
    }
}