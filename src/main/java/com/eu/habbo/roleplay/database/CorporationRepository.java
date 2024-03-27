package com.eu.habbo.roleplay.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.corporations.Corporation;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class CorporationRepository {

    private static CorporationRepository instance;

    public static CorporationRepository getInstance() {
        if (instance == null) {
            instance = new CorporationRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorporationRepository.class);

    public TIntObjectHashMap<Corporation> getAllCorporations() {
        TIntObjectHashMap<Corporation> corps = new TIntObjectHashMap<>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_corporations ORDER BY id ASC")) {
            while (set.next()) {
                corps.put(set.getInt("id"), new Corporation(set));
            }
            return corps;
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public void upsertCorporation(Corporation corporation) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_corporations (room_id, user_id, name, description, tags) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE room_id = VALUES(room_id), name = VALUES(name), description = VALUES(description), tags = VALUES(tags)")) {
                statement.setInt(1, corporation.getRoomID());
                statement.setInt(2, corporation.getUserID());
                statement.setString(3, corporation.getName());
                statement.setString(4, corporation.getDescription());
                statement.setString(5, String.join(";", corporation.getTags()));
                statement.setInt(6, corporation.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}
