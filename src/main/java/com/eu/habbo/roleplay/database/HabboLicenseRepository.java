package com.eu.habbo.roleplay.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.government.LicenseType;
import com.eu.habbo.roleplay.users.HabboLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class HabboLicenseRepository {
    private static HabboLicenseRepository instance;

    public static HabboLicenseRepository getInstance() {
        if (instance == null) {
            instance = new HabboLicenseRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboLicenseRepository.class);

    public HabboLicense getByID(int licenseID) {
        String sqlSelect = "SELECT * FROM rp_users_licenses WHERE id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(sqlSelect)) {

            selectStatement.setInt(1, licenseID);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return HabboLicense.fromSet(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public HabboLicense create(LicenseType licenseType, int userID) {
        long THIRTY_DAYS =  30L * 24 * 60 * 60;
        long expiresOn = (System.currentTimeMillis() / 1000) + THIRTY_DAYS;
        long createdAt = System.currentTimeMillis()  / 1000;
        long updatedAt = createdAt;

        String sqlInsert = "INSERT INTO rp_users_licenses (license_type, user_id, expires_on, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, licenseType.getValue());
            statement.setInt(2, userID);
            statement.setLong(3, expiresOn);
            statement.setLong(4, createdAt);
            statement.setLong(5, updatedAt);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new HabboLicense(id, userID, licenseType, expiresOn, createdAt, updatedAt);
                } else {
                    throw new SQLException("Creating user license failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public void delete(int licenseTypeValue, int userID) {
        String sqlDelete = "DELETE FROM rp_users_licenses WHERE license_type = ? AND user_id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
            statement.setInt(1, licenseTypeValue);
            statement.setInt(2, userID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}
