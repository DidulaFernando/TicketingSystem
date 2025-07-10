Project Structure

ticket-management-system/
├── client/                     # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/     # Angular components
│   │   │   │   ├── configuration/         # Configuration-related UI
│   │   │   │   │   ├── configuration.component.ts
│   │   │   │   │   ├── configuration.component.html
│   │   │   │   │   ├── configuration.component.css
│   │   │   │   └── ...
│   │   │   ├── services/       # Angular services for backend communication
│   │   ├── environments/       # Environment configuration files
│   │   ├── main.ts             # Angular main entry point
│   ├── angular.json            # Angular project configuration
│   ├── package.json            # NPM dependencies for the Angular frontend
│   └── tsconfig.json           # TypeScript configuration for the client
├── server/                     # Node.js backend
│   ├── routes/
│   │   ├── configurationRoutes.js     # Routes for saving/loading configurations
│   ├── services/
│   │   ├── databaseService.js         # SQLite database service
│   ├── app.js                  # Express.js app entry point
│   ├── package.json            # NPM dependencies for the Node.js backend
│   └── configurations.db       # SQLite database file
├── spring-boot/                # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com.example.ticketsystem/
│   │   │   │       ├── controller/
│   │   │   │       └── service/
│   │   │   ├── resources/
│   ├── pom.xml                 # Maven configuration file for Spring Boot
├── README.md                   # Project documentation

2.Prerequisites
Ensure the following is installed on your machine:
Node.js ≥ 16.x
npm ≥ 8.x
Angular CLI ≥ 17.x (`npm install -g @angular/cli`)
Java ≥ 17 and Maven
SQLite3 for database
Postman (optional, for testing API endpoints)

3. Setting up the Node.js Backend (Server)

Navigate to the `server/` directory and install dependencies. Then, start the server.

cd server
npm install
node app.js

4. Setting up the angular frontend

Navigate to the `client/` directory and install dependencies. Then, start the Angular development server.

cd client
npm install
ng serve

5. Setting up SpringBoot Backend

Navigate to the `spring-boot/` directory and start the Spring Boot service.

cd spring-boot
mvn spring-boot:run

The Spring Boot backend runs at [http://localhost:8080]()

6. Usage Instructions

Frontend (Client) Usage
1. Open [http://localhost:4200]() in your browser.
2. Use the Configuration Panel to:
    - Define ticket configuration parameters.
    - Add, edit, or delete vendors and customers.

 Save and load configurations directly with the Save Configuration and Load Configuration buttons.

API Endpoints
Node.js Backend APIs
-POST`/api/save-full-configuration`: Save the full configuration (vendors, customers, and ticket data).
- GET `/api/load-full-configuration`: Retrieve the latest configuration from the database.

Spring Boot APIs
- POST `/api/start`: Start the ticket system with the current configuration.
- POST `/api/stop`: Stop the running ticket system.