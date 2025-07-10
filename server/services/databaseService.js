const sqlite3 = require('sqlite3').verbose();
const path = require('path');

class DatabaseService {
    constructor() {
        const dbPath = path.resolve(__dirname, '../configurations.db');
        this.db = new sqlite3.Database(dbPath, (err) => {
            if (err) {
                console.error('Failed to connect to database:', err.message);
            } else {
                console.log('Connected to SQLite database.');
            }
        });
    }

    init() {
        const createConfigTable = `
            CREATE TABLE IF NOT EXISTS configuration (
                                                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                         totalTickets INTEGER NOT NULL,
                                                         ticketReleaseRate INTEGER NOT NULL,
                                                         customerRetrievalRate INTEGER NOT NULL,
                                                         maxTicketCapacity INTEGER NOT NULL,
                                                         createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
            )`;

        const createEntityTable = `
            CREATE TABLE IF NOT EXISTS entity_config (
                                                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                         name TEXT NOT NULL,
                                                         type TEXT CHECK(type IN ('Vendor', 'Customer')) NOT NULL,
                createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
                )`;

        this.db.run(createConfigTable);
        this.db.run(createEntityTable);
    }

    saveFullConfiguration(config, callback) {
        const { totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity, vendors, customers } = config;

        this.db.run('DELETE FROM configuration');
        this.db.run('DELETE FROM entity_config');

        const insertConfig = `
      INSERT INTO configuration (totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity)
      VALUES (?, ?, ?, ?)`;

        this.db.run(
            insertConfig,
            [totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity],
            (err) => {
                if (err) {
                    return callback(err);
                }

                const insertEntity = `INSERT INTO entity_config (name, type) VALUES (?, ?)`;
                const stmt = this.db.prepare(insertEntity);

                [...vendors.map((name) => ({ name, type: 'Vendor' })), ...customers.map((name) => ({ name, type: 'Customer' }))].forEach(({ name, type }) => {
                    stmt.run([name, type]);
                });

                stmt.finalize(callback);
            }
        );
    }

    loadFullConfiguration(callback) {
        const queryConfig = `SELECT * FROM configuration LIMIT 1`;
        const queryEntities = `SELECT name, type FROM entity_config`;

        this.db.get(queryConfig, (err, config) => {
            if (err || !config) {
                return callback(err || new Error('No configuration found.'));
            }

            this.db.all(queryEntities, (err, rows) => {
                if (err) {
                    return callback(err);
                }

                const vendors = rows.filter((row) => row.type === 'Vendor').map((row) => row.name);
                const customers = rows.filter((row) => row.type === 'Customer').map((row) => row.name);

                callback(null, { ...config, vendors, customers });
            });
        });
    }
}

module.exports = DatabaseService;