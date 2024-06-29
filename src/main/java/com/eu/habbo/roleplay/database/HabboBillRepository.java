package com.eu.habbo.roleplay.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.billing.UserBill;
import com.eu.habbo.roleplay.billing.items.BillType;
import com.eu.habbo.roleplay.billing.items.BillingItem;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class HabboBillRepository {
    private static HabboBillRepository instance;

    public static HabboBillRepository getInstance() {
        if (instance == null) {
            instance = new HabboBillRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboBillRepository.class);

    public UserBill getByID(int id) {
        String sqlSelect = "SELECT * FROM rp_users_bills WHERE id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect)) {

            selectStatement.setInt(1, id);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new UserBill(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public TIntObjectHashMap<UserBill> getByUserID(int userID) {
        TIntObjectHashMap<UserBill> userBills = new TIntObjectHashMap<>();
        String sqlSelect = "SELECT * FROM rp_users_bills WHERE user_id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect)) {

            selectStatement.setInt(1, userID);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    UserBill bill = new UserBill(resultSet);
                    userBills.put(bill.id, bill);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return userBills;
    }

    public  UserBill create(BillingItem billingItem) {
        return HabboBillRepository.getInstance().create(
                billingItem.userID(),
                billingItem.chargedByUserID(),
                billingItem.getChargedByCorpID(),
                billingItem.getType(),
                billingItem.getTitle(),
                billingItem.getDescription(),
                billingItem.getAmountOwed()
        );
    }

    public UserBill create(int userID, int chargedByUserID, int chargedByCorpID, BillType type, String title, String description, int amountOwed) {
        String sqlInsert = "INSERT INTO rp_users_bills (user_id, charged_by_user_id, charged_by_corp_id, type, title, description, amount_charged) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userID);
            statement.setInt(2, chargedByUserID);
            statement.setInt(3, chargedByCorpID);
            statement.setString(4, type.getValue());
            statement.setString(5, title);
            statement.setString(6, description);
            statement.setInt(7, amountOwed);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return getByID(generatedId);
                } else {
                    throw new SQLException("Creating billing statement failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public UserBill update(UserBill userBill) {
        String sqlUpdate = "UPDATE rp_users_bills SET amountPaid = ? WHERE id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {

            statement.setInt(1, userBill.amountPaid);
            statement.setInt(2, userBill.id);
            statement.executeUpdate();

            return userBill;
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public void delete(int id) {
        String sqlDelete = "DELETE FROM rp_users_bills WHERE id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}
