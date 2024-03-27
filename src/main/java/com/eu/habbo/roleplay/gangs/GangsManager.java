package com.eu.habbo.roleplay.gangs;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.corporations.Corporation;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GangsManager {

    private static GangsManager instance;

    public static GangsManager getInstance() {
        if (instance == null) {
            instance = new GangsManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GangsManager.class);

    private TIntObjectHashMap<Gang> gangs;

    public Gang getGangById(int gangID) {
        return this.gangs.get(gangID);
    }

    public Gang getGangByName(String gangName) {
        int[] keys = this.gangs.keys();
        for (int key : keys) {
            Gang gang = this.gangs.get(key);
            if (gang.getName().equalsIgnoreCase(gangName)) {
                return gang;
            }
        }
        return null;
    }

    public Gang createGangWithDefaultPosition(String gangName, int userID, int roomID) {
        Gang newGang = this.createGang(gangName, userID, roomID);
        GangPosition newPosition = this.createGangPosition(newGang.getId(), "Member");
        return newGang;
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
                    return this.loadGangByID(newGangId);
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
                    return this.loadGangPositionByID(newPositionId);

                } else {
                    throw new SQLException("Creating gang position failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    private Gang loadGangByID(int gangID) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM rp_gangs WHERE id = ?")) {
            selectStatement.setInt(1, gangID);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    Gang matchingGang = new Gang(resultSet);
                    this.gangs.put(gangID, matchingGang);
                    return matchingGang;
                } else {
                    throw new SQLException("Retrieving gang position failed, no records found.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    private GangPosition loadGangPositionByID(int gangPositionID) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM rp_gangs_positions WHERE id = ?")) {
            selectStatement.setInt(1, gangPositionID);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    GangPosition newPosition = new GangPosition(resultSet);
                    this.gangs.get(gangPositionID).addPosition(newPosition);
                    return newPosition;
                } else {
                    throw new SQLException("Retrieving gang position failed, no records found.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    private GangsManager() {
        long millis = System.currentTimeMillis();
        this.gangs = new TIntObjectHashMap<>();

        this.reload();

        LOGGER.info("Gangs Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public void reload() {
        this.loadGangs();
    }

    private void loadGangs() {
        this.gangs.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_gangs ORDER BY id ASC")) {
            while (set.next()) {
                Gang gang = null;
                if (!this.gangs.containsKey(set.getInt("id"))) {
                    gang = new Gang(set);
                    this.gangs.put(set.getInt("id"), gang);
                } else {
                    gang = this.gangs.get(set.getInt("id"));
                    gang.load(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void dispose() {
        this.gangs = null;
    }
}