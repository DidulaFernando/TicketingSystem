package org.example.server.service;

import org.example.server.model.TicketPool;
import org.example.server.model.Vendor;
import org.example.server.model.Customer;
import org.example.server.model.SystemConfig;
import org.example.server.util.ConsoleLogCapture;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class TicketSystemService {

    private static final Logger logger = LoggerFactory.getLogger(TicketSystemService.class);

    private ScheduledExecutorService executorService;
    private TicketPool ticketPool;
    private boolean completionLogged = false;

    public void startSystem(SystemConfig config) {
        logger.info("System starting with config: " + config);

        ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());

        List<String> vendorNames = config.getVendors();
        List<String> customerNames = config.getCustomers();

        executorService = Executors.newScheduledThreadPool(vendorNames.size() + customerNames.size() + 1);

        boolean customersFirst = config.getCustomerRetrievalRate() < config.getTicketReleaseRate();
        long vendorInitialDelay = customersFirst ? 1 : 0;
        long customerInitialDelay = customersFirst ? 0 : 1;

        for (String vendorName : vendorNames) {
            Vendor vendor = new Vendor(vendorName, ticketPool);
            executorService.scheduleWithFixedDelay(
                    vendor,
                    vendorInitialDelay,
                    config.getTicketReleaseRate(),
                    TimeUnit.MILLISECONDS);
        }

        for (String customerName : customerNames) {
            Customer customer = new Customer(ticketPool, config.getCustomerRetrievalRate(), customerName);
            executorService.scheduleWithFixedDelay(
                    customer::run,
                    customerInitialDelay,
                    config.getCustomerRetrievalRate(),
                    TimeUnit.MILLISECONDS);
        }

        executorService.scheduleWithFixedDelay(() -> {
            if (ticketPool.isSystemComplete() && !completionLogged) {
                logger.info("Vendors have released all tickets. Customers have bought all the tickets.");
                completionLogged = true;
                stopSystem();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void stopSystem() {
        if (executorService != null) {
            executorService.shutdownNow();
            ConsoleLogCapture.clearLogs(); // Clear all logs when the system stops
            logger.info("System stopped successfully.");
        }
    }

    public Map<String, Integer> getTicketStatus() {
        Map<String, Integer> status = new HashMap<>();
        if (ticketPool != null) {
            status.put("totalTicketsPurchased", ticketPool.getTotalTicketsPurchased());
            status.put("newTicketsReleased", ticketPool.getTotalTicketsReleased());
        } else {
            status.put("totalTicketsPurchased", 0);
            status.put("newTicketsReleased", 0);
        }
        return status;
    }

    public List<String> fetchLogs() {
        return ConsoleLogCapture.getLogs();
    }
}