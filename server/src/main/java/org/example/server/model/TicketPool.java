package org.example.server.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketPool {
    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);

    private final int maxPool;
    private int currentPool;
    private int remainingTickets;
    private int totalTicketsReleased; // Tracks total tickets released
    private int totalTicketsPurchased; // Tracks total tickets purchased
    private boolean salesComplete;
    private boolean addingComplete;

    public TicketPool(int maxPool, int totalTickets) {
        this.maxPool = maxPool;
        this.currentPool = 0;
        this.remainingTickets = totalTickets;
        this.totalTicketsReleased = 0; // Initialize to 0
        this.totalTicketsPurchased = 0; // Initialize to 0
        this.salesComplete = false;
        this.addingComplete = false;
    }

    public synchronized boolean addTickets(String vendorName, int ticketsToAdd) {
        if (addingComplete || currentPool + ticketsToAdd > maxPool || remainingTickets <= 0) {
            logger.warn(vendorName + " tried to add " + ticketsToAdd + " tickets, but the pool is full.");
            return false;
        }

        ticketsToAdd = Math.min(ticketsToAdd, remainingTickets);
        currentPool += ticketsToAdd;
        remainingTickets -= ticketsToAdd;
        totalTicketsReleased += ticketsToAdd; // Update total tickets released

        if (remainingTickets <= 0) {
            addingComplete = true;
        }

        logger.info(vendorName + " added " + ticketsToAdd + " tickets. Remaining: " + remainingTickets);
        notifyAll();
        return true;
    }

    public synchronized boolean removeTickets(int tickets, String customerName) {
        if (currentPool < tickets) {
            logger.warn(customerName + " wanted " + tickets + " tickets, but not enough in the pool.");
            return false;
        }

        currentPool -= tickets;
        totalTicketsPurchased += tickets; // Update total tickets purchased

        logger.info(customerName + " bought " + tickets + " tickets. Remaining pool: " + currentPool);

        if (remainingTickets <= 0 && currentPool == 0) {
            salesComplete = true;
        }

        notifyAll();
        return true;
    }

    public synchronized int getRemainingTicketsToRelease() {
        return remainingTickets;
    }

    public synchronized int getCurrentPool() {
        return currentPool;
    }

    public synchronized boolean isAddingComplete() {
        return addingComplete;
    }

    public synchronized boolean isSystemComplete() {
        return addingComplete && salesComplete;
    }

    public synchronized int getTotalTicketsReleased() {
        return totalTicketsReleased;
    }

    public synchronized int getTotalTicketsPurchased() {
        return totalTicketsPurchased;
    }
}