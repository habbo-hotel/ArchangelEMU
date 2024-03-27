package com.eu.habbo.roleplay.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.gangs.Gang;
import com.eu.habbo.roleplay.gangs.GangPosition;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;

public class GangRepository {

    private static GangRepository instance;

    public static GangRepository getInstance() {
        if (instance == null) {
            instance = new GangRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GangRepository.class);

    public TIntObjectHashMap<Gang> getAllGangs() {
        TIntObjectHashMap<Gang> gangs = new TIntObjectHashMap<>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_gangs ORDER BY id ASC")) {
            while (set.next()) {
                gangs.put(set.getInt("id"), new Gang(set));
            }
            return gangs;
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public Gang createGang(String gangName, int userID, int roomID) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_gangs (name, description, user_id, room_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, gangName);
            statement.setString(2, gangName);
            statement.setInt(3, userID);
            statement.setInt(4, roomID);
            statement.setLong(5, Instant.now().getEpochSecond());
            statement.setLong(6, Instant.now().getEpochSecond());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating gang failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newGangId = generatedKeys.getInt(1);
                    return this.getGangByID(newGangId);
                } else {
                    throw new SQLException("Creating gang failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

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
