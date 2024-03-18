package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class DatabaseLogger {
    private final ConcurrentLinkedQueue<DatabaseLoggable> loggables = new ConcurrentLinkedQueue<>();

    public void store(DatabaseLoggable loggable) {
        this.loggables.add(loggable);
    }

    public void save() {
        if (Emulator.getDatabase() == null || Emulator.getDatabase().getDataSource() == null) {
            return;
        }

        if (this.loggables.isEmpty()) {
            return;
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            while (!this.loggables.isEmpty()) {
                DatabaseLoggable loggable = this.loggables.remove();

                try (PreparedStatement statement = connection.prepareStatement(loggable.getQuery())) {
                    loggable.log(statement);
                    statement.executeBatch();
                }

            }
        } catch (SQLException e) {
            log.error("Exception caught while saving loggables to database.", e);
        }
    }

}
