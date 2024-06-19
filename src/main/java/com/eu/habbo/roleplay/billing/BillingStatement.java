package com.eu.habbo.roleplay.billing;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.billing.items.BillingItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class BillingStatement {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillingStatement.class);

    public static BillingStatement create(BillingItem billingItem) {
        return BillingStatement.create(billingItem.getUserID(), billingItem.getChargedByUserID(), billingItem.getChargedByCorpID(), billingItem.getTitle(), billingItem.getDescription(), billingItem.getAmountOwed());
    }

    public static BillingStatement create(int userID, int chargedByUserID, int chargedByCorpID, String title, String description, int amountOwed) {
        String sqlInsert = "INSERT INTO rp_users_bills (user_id, charged_by_user_id, charged_by_corp_id, title, description, amount_charged) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userID);
            statement.setInt(2, chargedByUserID);
            statement.setInt(3, chargedByCorpID);
            statement.setString(4, title);
            statement.setString(5, description);
            statement.setInt(6, amountOwed);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return BillingStatement.loadById(generatedId);
                } else {
                    throw new SQLException("Creating billing statement failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public static BillingStatement loadById(int id) {
        String sqlSelect = "SELECT * FROM rp_users_bills WHERE id = ?";

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect)) {

            // Set the parameter for the SELECT statement
            selectStatement.setInt(1, id);

            // Execute the select statement and get the result set
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new BillingStatement(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public int id;
    public int userID;
    public int chargedByUserID;
    public int chargedByCorpID;
    public String title;
    public String description;
    public int amountCharged;
    public int amountPaid;

    public BillingStatement(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.userID = set.getInt("user_id");
        this.chargedByUserID = set.getInt("charged_by_user_id");
        this.chargedByCorpID = set.getInt("charged_by_corp_id");
        this.title = set.getString("title");
        this.description = set.getString("description");
        this.amountCharged = set.getInt("amount_charged");
        this.amountPaid = set.getInt("amount_paid");
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
