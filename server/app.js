const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors'); // Import CORS package
const configurationRoutes = require('./routes/configurationRoutes');

const app = express();
const PORT = 8090;

// Enable CORS for requests from http://localhost:4200
app.use(cors({ origin: 'http://localhost:4200' }));

// Middleware to parse incoming JSON requests
app.use(bodyParser.json());

// Use the configuration routes under the /api path
app.use('/api', configurationRoutes);

// Start the server
app.listen(PORT, () => {
    console.log(`Server is running at http://localhost:${PORT}`);
});