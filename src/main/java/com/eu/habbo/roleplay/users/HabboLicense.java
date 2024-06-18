package com.eu.habbo.roleplay.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.government.License;
import com.eu.habbo.roleplay.government.LicenseManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class HabboLicense {

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboWeapon.class);

    private License license;

    private int licenseID;

    private int userID;

    public HabboLicense(ResultSet set) throws SQLException {
        this.license = LicenseManager.getInstance().getLicenseByID(set.getInt("license_id"));
        this.licenseID = set.getInt("weapon_id");
        this.userID = set.getInt("user_id");
    }

    public HabboLicense(int licenseID, int userID) {
        this.licenseID = this.licenseID;
        this.userID = userID;
    }

    public void insert() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_users_licenses (license_type, user_id, expires_on) VALUES (?, ?, ?)")) {
            statement.setInt(1, this.license.getLicenseType());
            statement.setInt(2, this.userID);
            statement.setInt(3, this.license.getExpiresOn());
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void delete() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_users_licenses WHERE license_type = ? AND user_id = ?")) {
            statement.setInt(1, this.license.getLicenseType());
            statement.setInt(2, this.userID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}