import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class  Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static Configuration configuration = new Configuration();
    private static final String CONFIG_FILE = "config.txt";
    private static TicketPool ticketPool;

    public static void main(String[] args) {
        loadConfiguration();

        boolean systemRunning = false;

        while (true) {
            if (!systemRunning) {
                System.out.println("\nMenu:");
                System.out.println("1. Enter Configuration");
                System.out.println("2. Save Configuration");
                System.out.println("3. Start System");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> enterConfiguration();
                case 2 -> saveConfiguration();
                case 3 -> {
                    startSystem();
                    systemRunning = true;
                }
                case 4 -> {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void enterConfiguration() {
        System.out.print("Enter total ticket count: ");
        configuration.totalTickets = getValidInputGreaterThanZero();

        System.out.print("Enter ticket release rate (ms): ");
        configuration.ticketReleaseRate = getValidInputGreaterThanZero();

        System.out.print("Enter customer retrieval rate (ms): ");
        configuration.customerRetrievalRate = getValidInputGreaterThanZero();

        System.out.print("Enter maximum ticket count: ");
        configuration.maxTickets = getValidInputGreaterThanZero();
    }

    String Name = "Didula";
    private static void enterConfiguration(String Name){
        System.out.print("Enter total ticket count: ");
    }

    private static int getValidInputGreaterThanZero() {
        int input;
        while (true) {
            try {
                input = scanner.nextInt();
                if (input <= 0) {
                    System.out.println("Invalid input! Please enter a positive integer greater than 0.");
                } else {
                    return input;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid integer.");
                scanner.next();
            }
        }
    }

    private static void saveConfiguration() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            writer.write(configuration.totalTickets + "\n");
            writer.write(configuration.ticketReleaseRate + "\n");
            writer.write(configuration.customerRetrievalRate + "\n");
            writer.write(configuration.maxTickets + "\n");
            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }

    private static void loadConfiguration() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            System.out.println("No existing configuration found. Using defaults.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            configuration.totalTickets = Integer.parseInt(reader.readLine());
            configuration.ticketReleaseRate = Integer.parseInt(reader.readLine());
            configuration.customerRetrievalRate = Integer.parseInt(reader.readLine());
            configuration.maxTickets = Integer.parseInt(reader.readLine());
            System.out.println("Configuration loaded successfully.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading configuration: " + e.getMessage());
        }
    }

    private static void startSystem() {
        System.out.println("\nStarting the system...\n");

        ticketPool = new TicketPool(configuration.maxTickets, configuration.totalTickets);

        CountDownLatch latch = new CountDownLatch(1);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        // Determine which action should have a delayed start based on specified rates
        boolean isCustomerFaster = configuration.customerRetrievalRate < configuration.ticketReleaseRate;

        int initialVendorDelay = isCustomerFaster ? configuration.customerRetrievalRate : 0;
        int initialCustomerDelay = isCustomerFaster ? 0 : configuration.ticketReleaseRate;

        // Create customers with unique IDs, respecting initial delay
        for (int i = 0; i < 5; i++) {
            String customerId = String.valueOf(i + 1);
            executor.scheduleWithFixedDelay(new Customer(ticketPool, configuration.customerRetrievalRate, customerId, latch),
                    initialCustomerDelay, configuration.customerRetrievalRate, TimeUnit.MILLISECONDS);
        }

        // Create vendors with unique IDs, respecting initial delay
        for (int i = 0; i < 5; i++) {
            String vendorId = String.valueOf(i + 1);
            executor.scheduleWithFixedDelay(new Vendor(vendorId, ticketPool),
                    initialVendorDelay, configuration.ticketReleaseRate, TimeUnit.MILLISECONDS);
        }

        latch.countDown();

        executor.scheduleWithFixedDelay(() -> {
            if (ticketPool.isSystemComplete()) {
                System.out.println("\nVendors have added all tickets.\nCustomers are done buying tickets.\n");
                executor.shutdownNow();
                System.exit(0);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}