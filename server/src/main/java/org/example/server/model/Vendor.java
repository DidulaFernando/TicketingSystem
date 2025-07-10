package org.example.server.model;

import java.util.Random;

public class Vendor implements Runnable {
    private final String vendorName;
    private final TicketPool ticketPool;
    private final Random random = new Random();

    public Vendor(String vendorName, TicketPool ticketPool) {
        this.vendorName = vendorName;
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (ticketPool) {
                    if (ticketPool.isSystemComplete() || ticketPool.isAddingComplete()) {
                        break; // Stop adding tickets if the system is complete
                    }
                }

                int ticketsToAdd = random.nextInt(5) + 1; // Random number between 1 and 5

                // Try to add tickets
                ticketPool.addTickets(vendorName, ticketsToAdd);

                // Wait before retry
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}