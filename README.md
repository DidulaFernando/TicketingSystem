# Realtime Ticketing System

A comprehensive ticketing system simulation with three main components:
- **CLI (Java):** A pure Java, multithreaded simulation of vendors releasing tickets and customers purchasing them, ensuring thread safety and pool capacity constraints.
- **Web Application:** 
  - **Frontend:** Built with Angular, providing a real-time dashboard for configuration, live updates, and log viewing.
  - **Backend:** Powered by Spring Boot (Java) for simulation logic and Node.js/Express for configuration persistence with SQLite.

---

## Features

- **Multithreaded Simulation:** Vendors and customers operate concurrently, with synchronized access to a shared ticket pool.
- **Capacity Enforcement:** Ensures ticket pool never exceeds its maximum capacity and tickets are not oversold.
- **Real-Time Dashboard:** Angular frontend displays live ticket statistics and logs.
- **Configurable Entities:** Easily add/remove vendors and customers, and adjust simulation parameters.
- **Persistent Configuration:** Save/load system configurations and entities using a Node.js backend with SQLite.
- **Log Streaming:** View real-time logs of ticket operations in the web UI.

---

## Project Structure

```
ticket-management-system/
├── CLI/                # Pure Java CLI version (multithreaded simulation)
│   ├── src/
│   └── config.txt
├── client/             # Angular frontend
│   ├── src/
│   └── package.json
├── server/             # Spring Boot backend (simulation) & Node.js backend (config/db)
│   ├── src/
│   ├── app.js          # Node.js backend entry
│   ├── pom.xml         # Spring Boot Maven config
│   └── configurations.db
└── README.md
```

---

## Getting Started

### Prerequisites

- **Java 17+** (for CLI and Spring Boot)
- **Node.js 16+** and **npm 8+** (for Angular and Node.js backend)
- **Angular CLI 17+** (`npm install -g @angular/cli`)
- **SQLite3** (for configuration database)

---

### 1. Running the CLI (Java) Version

1. Navigate to the `CLI/` directory.
2. Compile and run the Java program:
   ```sh
   javac -d out src/*.java
   java -cp out Main
   ```
3. Follow the menu prompts to configure and start the simulation.

---

### 2. Running the Web Application

#### A. Start the Node.js Backend (Configuration/Database)

```sh
cd server
npm install
node app.js
```
- Runs on [http://localhost:8090](http://localhost:8090)

#### B. Start the Spring Boot Backend (Simulation Engine)

```sh
cd server
mvn spring-boot:run
```
- Runs on [http://localhost:8080](http://localhost:8080)

#### C. Start the Angular Frontend

```sh
cd client
npm install
ng serve
```
- Access the UI at [http://localhost:4200](http://localhost:4200)

---

## Usage

1. **Configure the System:**  
   - Set ticket parameters, add vendors/customers in the Angular UI.
2. **Save/Load Configurations:**  
   - Use "Save" and "Load" buttons to persist or retrieve settings/entities.
3. **Start/Stop Simulation:**  
   - Use "Start System" to begin the simulation (Spring Boot backend).
   - Use "End System" to stop it.
4. **Monitor Live Updates:**  
   - View real-time ticket statistics and logs in the dashboard.

---

## Technical Highlights

- **Concurrency:**  
  Java's `synchronized` blocks and thread-safe design prevent race conditions in ticket allocation.
- **Frontend-Backend Communication:**  
  Angular communicates with Spring Boot for simulation and Node.js for configuration via REST APIs.
- **Database:**  
  SQLite is used for lightweight, persistent storage of configurations and entities.
- **Real-Time Logs:**  
  Server-Sent Events (SSE) stream logs to the frontend for live monitoring.

---

## License

This project is for educational purposes.

---

## Authors

- Didula Fernando (https://github.com/DidulaFernando)
