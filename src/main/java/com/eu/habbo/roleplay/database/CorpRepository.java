package com.eu.habbo.roleplay.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.corp.Corp;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class CorpRepository {

    private static CorpRepository instance;

    public static CorpRepository getInstance() {
        if (instance == null) {
            instance = new CorpRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpRepository.class);

    public TIntObjectHashMap<Corp> getAllCorps() {
        TIntObjectHashMap<Corp> corps = new TIntObjectHashMap<>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_corporations ORDER BY id ASC")) {
            while (set.next()) {
                corps.put(set.getInt("id"), new Corp(set));
            }
            return corps;
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public void upsertCorp(Corp corp) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_corporations (guild_id, tags) VALUES (?, ?) ON DUPLICATE KEY UPDATE guild_id = VALUES(guild_id), name = VALUES(name), tags = VALUES(tags)")) {
                statement.setInt(1, corp.getGuild().getId());
                statement.setString(2, String.join(";", corp.getTags()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}