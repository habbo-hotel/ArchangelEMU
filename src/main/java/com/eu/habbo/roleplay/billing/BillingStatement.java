package com.eu.habbo.roleplay.billing;

import com.eu.habbo.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillingStatement {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillingStatement.class);

    public static BillingStatement create(int userID, int chargedByUserID, int chargedByCorpID, String title, String description, int amountCharged) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_users_bills (user_id, charged_by_user_id, charged_by_corp_id, title, description, amountCharged) VALUES (?, ?)")) {
            statement.setInt(1, userID);
            statement.setInt(2, chargedByUserID);
            statement.setInt(3, chargedByCorpID);
            statement.setString(4, title);
            statement.setString(5, description);
            statement.setInt(6, amountCharged);
            statement.execute();
            return BillingStatement.load(statement.getResultSet());
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public static BillingStatement load(ResultSet set) throws SQLException {
        return new BillingStatement(set);
    }

    public int id;
    public int userID;
    public int chargedByUserID;
    public int chargedByCorpID;
    public String title;
    public String description;
    public int amountCharged;
    public int amountPaid;
    public int createdAt;
    public int updatedAt;

    public BillingStatement(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.userID = set.getInt("user_id");
        this.chargedByUserID = set.getInt("charged_by_user_id");
        this.chargedByCorpID = set.getInt("charged_by_corp_id");
        this.title = set.getString("title");
        this.description = set.getString("description");
        this.amountCharged = set.getInt("amount_charged");
        this.amountPaid = set.getInt("amount_paid");
        this.createdAt = set.getInt("created_at");
        this.updatedAt = set.getInt("updated_at");
    }

    public void save() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE rp_users_bills SET amountPaid = ? WHERE id = ?")) {
            statement.setInt(1, this.amountPaid);
            statement.setInt(1, this.id);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void delete() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_users_bills WHERE weapon_id = ? AND user_id = ?")) {
            statement.setInt(1, this.id);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}
