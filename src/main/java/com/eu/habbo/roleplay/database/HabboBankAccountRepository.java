package com.eu.habbo.roleplay.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.users.HabboBankAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class HabboBankAccountRepository {
    private static HabboBankAccountRepository instance;

    public static HabboBankAccountRepository getInstance() {
        if (instance == null) {
            instance = new HabboBankAccountRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboBankAccountRepository.class);

    public HabboBankAccount getByUserAndCorpID(int userID, int corpID) {
        String sqlSelect = "SELECT * FROM rp_users_bank_accs WHERE user_id = ? AND corp_id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect)) {

            selectStatement.setInt(1, userID);
            selectStatement.setInt(2, corpID);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new HabboBankAccount(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public HabboBankAccount getByUserID(int userID) {
        String sqlSelect = "SELECT * FROM rp_users_bank_accs WHERE user_id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect)) {

            selectStatement.setInt(1, userID);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new HabboBankAccount(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public HabboBankAccount create(HabboBankAccount habboBankAccount) {
        return this.create(
                habboBankAccount.getUserID(),
                habboBankAccount.getCorpID(),
                habboBankAccount.getCheckingBalance(),
                habboBankAccount.getCreatedAt(),
                habboBankAccount.getUpdatedAt()
        );
    }

    public HabboBankAccount create(int userID, int corpID, int creditBalance, int createdAt, int updatedAt) {
        String sqlInsert = "INSERT INTO rp_users_stats (user_id, corp_id, credit_balance, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userID);
            statement.setInt(2, corpID);
            statement.setInt(3, creditBalance);
            statement.setInt(4, createdAt);
            statement.setInt(5, updatedAt);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return this.getByUserID(userID);
                } else {
                    throw new SQLException("Creating billing statement failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public void update(HabboBankAccount habboBankAccount) {
        String sqlUpdate = "UPDATE rp_users_bank_accs SET credit_balance = ?, updated_at = ? WHERE id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {

            int updatedAt = (int) (System.currentTimeMillis() / 1000);

            statement.setInt(1, habboBankAccount.getCheckingBalance());
            statement.setInt(2, updatedAt);
            statement.setInt(3, habboBankAccount.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
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
