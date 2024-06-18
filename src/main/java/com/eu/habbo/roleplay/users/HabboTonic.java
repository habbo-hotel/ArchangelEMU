package com.eu.habbo.roleplay.users;

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
public class HabboTonic {

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboTonic.class);

    private Tonic tonic;

    private int tonicID;

    private int userID;

    public HabboTonic(ResultSet set) throws SQLException {
        this.tonic = TonicsManager.getInstance().getTonicByID(set.getInt("tonic_id"));
        this.tonicID = set.getInt("tonic_id");
        this.userID = set.getInt("user_id");
    }

    public HabboTonic(int tonicID, int userID) {
        this.tonicID = tonicID;
        this.userID = userID;
    }

    public void insert() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_users_tonics (tonic_id, user_id) VALUES (?, ?)")) {
            statement.setInt(1, this.tonicID);
            statement.setInt(2, this.userID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void delete() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_users_tonics WHERE tonic_id = ? AND user_id = ?")) {
            statement.setInt(1, this.tonicID);
            statement.setInt(2, this.userID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}