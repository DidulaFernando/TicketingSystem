package org.example.server.model;

public class Configuration {
    private int totalTickets = 100;  // Default value example
    private int ticketReleaseRate = 1000;
    private int customerRetrievalRate = 1000;
    private int maxTickets = 10;

    // Getter and setter for totalTickets
    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    // Getter and setter for ticketReleaseRate
    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    // Getter and setter for customerRetrievalRate
    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    // Getter and setter for maxTickets
    public int getMaxTickets() {
        return maxTickets;
    }

    public void setMaxTickets(int maxTickets) {
        this.maxTickets = maxTickets;
    }
}