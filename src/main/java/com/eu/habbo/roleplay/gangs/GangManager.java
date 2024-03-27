package com.eu.habbo.roleplay.gangs;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;

public class GangManager {

    private static GangManager instance;

    public static GangManager getInstance() {
        if (instance == null) {
            instance = new GangManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GangManager.class);

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
        this.gangs.put(newGang.getId(), newGang);
        GangPosition newPosition = GangRepository.getInstance().createGangPosition(newGang.getId(), "Member");
        this.gangs.get(newGang.getId()).addPosition(newPosition);
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
                    return GangRepository.getInstance().getGangByID(newGangId);
                } else {
                    throw new SQLException("Creating gang failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }



    private GangManager() {
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