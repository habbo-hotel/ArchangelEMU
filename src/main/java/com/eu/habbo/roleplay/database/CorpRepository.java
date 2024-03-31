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
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_corporations ORDER BY guild_id ASC")) {
            while (set.next()) {
                corps.put(set.getInt("guild_id"), new Corp(set));
            }
            return corps;
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public Corp getCorpByGuildID(int guildID) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_corporations WHERE guild_id = ? ORDER BY guild_id ASC LIMIT 1")) {
            statement.setInt(1, guildID);

            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    return new Corp(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public void upsertCorp(Corp corp) {
        this.upsertCorp(corp.getGuild().getId(), corp.getTags().toString());
    }

    public void upsertCorp(int guildID, String tags) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_corporations (guild_id, tags) VALUES (?, ?) ON DUPLICATE KEY UPDATE guild_id = VALUES(guild_id), tags = VALUES(tags)")) {
                statement.setInt(1, guildID);
                statement.setString(2, String.join(";", tags));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}