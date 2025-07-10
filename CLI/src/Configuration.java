package com.example.lab.based.practical.mock.solutions;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class ClientServer {

    // Logger to track events
    private static final Logger logger = Logger.getLogger(ClientServer.class.getName());

    // Constant for the port number
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // Creating and starting the server thread
        Thread serverThread = new Thread(new Server());
        serverThread.start();
        logger.info("Server thread started");

        // Creating and starting the client thread
        Thread clientThread = new Thread(new Client());
        clientThread.start();
        logger.info("Client thread started");
    }

    // Nested class for the Server
    static class Server implements Runnable {
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                logger.info("Server started on port " + PORT);

                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        logger.info("Client connected: " + clientSocket);

                        // Starting a new thread to handle communication with the client
                        Thread clientHandlerThread = new Thread(new ClientHandler(clientSocket));
                        clientHandlerThread.start();
                    } catch (IOException e) {
                        logger.severe("Error accepting client connection: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                logger.severe("Error starting server: " + e.getMessage());
            }
        }
    }

    // Nested class for handling client interactions
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor takes client socket
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;

                // Reading messages from the client
                while ((inputLine = reader.readLine()) != null) {
                    logger.info("Server received: " + inputLine);

                    if ("exit".equalsIgnoreCase(inputLine)) {
                        writer.println("Bye");
                        logger.info("Sent 'Bye' to client");
                        break;
                    }

                    // Send echo response to client
                    writer.println("Echo: " + inputLine);
                    logger.info("Sent 'Echo' response to client: " + inputLine);
                }
            } catch (IOException e) {
                logger.severe("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    logger.info("Client socket closed");
                } catch (IOException e) {
                    logger.severe("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    // Nested class for the Client
    static class Client implements Runnable {
        @Override
        public void run() {
            try (Socket socket = new Socket("localhost", PORT);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

                String userInput;
                String serverResponse;

                // Read user input from the console and send it to the server
                while ((userInput = consoleReader.readLine()) != null) {
                    writer.println(userInput);
                    logger.info("Sent to server: " + userInput);

                    // Read the server's response
                    serverResponse = reader.readLine();
                    if (serverResponse == null) {
                        logger.info("Server closed the connection.");
                        break;
                    }

                    // Print server's response
                    System.out.println("Server: " + serverResponse);

                    // Check if user typed "exit"
                    if ("exit".equalsIgnoreCase(userInput)) {
                        logger.info("Exiting client...");
                        break;
                    }
                }
            } catch (IOException e) {
                logger.severe("Error in client: " + e.getMessage());
            }
        }
    }
}


package com.example.lab.based.practical.mock.http;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    public static void main(String[] args) {
        try {
            // Creating HttpServer instance
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            System.out.println("Server is being set up on port 8080...");

            // Creating context for "/myendpoint"
            server.createContext("/myendpoint", new MyHandler());

            // Setting executor to null (no thread pool)
            server.setExecutor(null);

            // Start the server
            server.start();
            System.out.println("Server started on port 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Static inner class to handle requests
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Getting request method
            String method = exchange.getRequestMethod();
            System.out.println("Request Method: " + method); // Log method

            // Getting request URI path
            String path = exchange.getRequestURI().getPath();
            System.out.println("Request URI Path: " + path); // Log URI Path

            // Handle GET request
            if ("GET".equalsIgnoreCase(method)) {
                System.out.println("Handling GET request...");
                handleGetRequest(exchange);
                return;
            }

            // Handle POST request
            if ("POST".equalsIgnoreCase(method)) {
                System.out.println("Handling POST request...");
                String response = "This is a POST request to /myendpoint";
                sendResponse(exchange, response, 200);  // Send POST response
                return;
            }

            // Handle unsupported methods
            System.out.println("Unsupported request method: " + method);
            String unsupportedResponse = "Unsupported request method: " + method;
            sendResponse(exchange, unsupportedResponse, 405); // Method Not Allowed
        }

        // Method to handle GET requests
        private void handleGetRequest(HttpExchange exchange) throws IOException {
            String response = "This is a GET request to /myendpoint";
            sendResponse(exchange, response, 200); // Send GET response
        }

        // Method to send the response
        private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
            System.out.println("Sending response: " + response); // Log response being sent

            // Set response headers
            exchange.getResponseHeaders().set("Content-Type", "text/plain");

            // Send response code and headers
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);

            // Send the response body
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            System.out.println("Response sent with status code: " + statusCode); // Log after response is sent
        }
    }
}
