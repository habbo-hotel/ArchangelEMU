package com.eu.habbo.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.ConfigurationManager;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private HikariDataSource dataSource;
    private DatabasePool databasePool;

    public Database(ConfigurationManager config) {
        long millis = System.currentTimeMillis();

        boolean SQLException = false;

        try {
            this.databasePool = new DatabasePool();
            if (!this.databasePool.getStoragePooling(config)) {
                LOGGER.info("Failed to connect to the database. Please check config.ini and make sure the MySQL process is running. Shutting down...");
                SQLException = true;
                return;
            }
            this.dataSource = this.databasePool.getDatabase();
        } catch (Exception e) {
            SQLException = true;
            LOGGER.error("Failed to connect to your database.", e);
        } finally {
            if (SQLException) {
                Emulator.prepareShutdown();
            }
        }

        LOGGER.info("Database -> Connected! ({} MS)", System.currentTimeMillis() - millis);
    }

    public void dispose() {
        if (this.databasePool != null) {
            this.databasePool.getDatabase().close();
        }

        this.dataSource.close();
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public DatabasePool getDatabasePool() {
        return this.databasePool;
    }
}
