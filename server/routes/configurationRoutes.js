const express = require('express');
const router = express.Router();
const DatabaseService = require('../services/databaseService');

const dbService = new DatabaseService();
dbService.init();

// POST /api/save-full-configuration
router.post('/save-full-configuration', (req, res) => {
    const {
        totalTickets,
        ticketReleaseRate,
        customerRetrievalRate,
        maxTicketCapacity,
        vendors,
        customers,
    } = req.body;

    if (
        !totalTickets ||
        !ticketReleaseRate ||
        !customerRetrievalRate ||
        !maxTicketCapacity
    ) {
        return res.status(400).send('Missing configuration parameters.');
    }

    dbService.saveFullConfiguration(
        {
            totalTickets,
            ticketReleaseRate,
            customerRetrievalRate,
            maxTicketCapacity,
            vendors,
            customers,
        },
        (err) => {
            if (err) {
                return res.status(500).send('Failed to save configuration.');
            }
            res.status(200).send('Configuration saved successfully.');
        }
    );
});

// GET /api/load-full-configuration
router.get('/load-full-configuration', (req, res) => {
    dbService.loadFullConfiguration((err, config) => {
        if (err) {
            return res.status(500).send('Failed to load configuration.');
        }
        res.status(200).json(config);
    });
});

module.exports = router;