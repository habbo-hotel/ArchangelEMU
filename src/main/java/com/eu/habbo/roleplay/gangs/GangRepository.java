package com.eu.habbo.roleplay.gangs;

import com.eu.habbo.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class GangRepository {

    private static GangRepository instance;

    public static GangRepository getInstance() {
        if (instance == null) {
            instance = new GangRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GangRepository.class);

    public GangPosition createGangPosition (int gangID, String positionName) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_gangs_positions (gang_id, name, description) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, gangID);
            statement.setString(2, positionName);
            statement.setString(3, positionName);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating gang position failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newPositionId = generatedKeys.getInt(1);
                    return this.getGangPositionByID(newPositionId);

                } else {
                    throw new SQLException("Creating gang position failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public Gang getGangByID(int gangID) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM rp_gangs WHERE id = ?")) {
            selectStatement.setInt(1, gangID);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Gang(resultSet);
                } else {
                    throw new SQLException("Retrieving gang position failed, no records found.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public GangPosition getGangPositionByID(int gangPositionID) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM rp_gangs_positions WHERE id = ?")) {
            selectStatement.setInt(1, gangPositionID);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new GangPosition(resultSet);
                } else {
                    throw new SQLException("Retrieving gang position failed, no records found.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }
    public void deleteGangByID(int gangID) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteGangStatement = connection.prepareStatement("DELETE FROM rp_gangs WHERE id = ?")) {
                deleteGangStatement.setInt(1, gangID);
                int affectedRows = deleteGangStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Deleting gang failed, no rows affected.");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

}
