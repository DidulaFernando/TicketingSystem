package org.example.server.model;

import java.util.Random;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrievalRate;
    private final String customerId;
    private final Random random = new Random();

    public Customer(TicketPool ticketPool, int retrievalRate, String customerId) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (ticketPool) {
                    if (ticketPool.isSystemComplete()) {
                        break; // Stop logging if the system is complete
                    }
                }

                int ticketsToBuy = random.nextInt(5) + 1; // Random between 1 and 5

                // Try to remove tickets
                ticketPool.removeTickets(ticketsToBuy, customerId);

                // Sleep for rate and retry
                Thread.sleep(retrievalRate);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}