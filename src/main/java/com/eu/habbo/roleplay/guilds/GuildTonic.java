package com.eu.habbo.roleplay.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.tonics.Tonic;
import com.eu.habbo.roleplay.tonics.TonicsManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class GuildTonic {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildTonic.class);

    private Tonic tonic;

    private int tonicID;

    private int guildID;

    public GuildTonic(ResultSet set) throws SQLException {
        this.tonic = TonicsManager.getInstance().getTonicByID(set.getInt("tonic_id"));
        this.tonicID = set.getInt("tonic_id");
        this.guildID = set.getInt("guild_id");
    }

    public GuildTonic(int tonicID, int guildID) {
        this.tonicID = tonicID;
        this.guildID = guildID;
    }

    public void insert() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_guilds_tonics (tonic_id, guildID) VALUES (?, ?)")) {
            statement.setInt(1, this.tonicID);
            statement.setInt(2, this.guildID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void delete() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_guilds_tonics WHERE tonic_id = ? AND guildID = ?")) {
            statement.setInt(1, this.tonicID);
            statement.setInt(2, this.guildID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}