package com.eu.habbo.roleplay.government;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class LicenseManager {

    private static LicenseManager instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseManager.class);

    public static LicenseManager getInstance() {
        if (instance == null) {
            instance = new LicenseManager();
        }
        return instance;
    }

    private TIntObjectHashMap<License> licenses;

    private LicenseManager() {
        long millis = System.currentTimeMillis();
        this.licenses = new TIntObjectHashMap<>();
        this.loadLicenses();
        LOGGER.info("License Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public License getLicenseByID(int licenseID) {
        return this.licenses.get(licenseID);
    }

    public void reload() {
        this.loadLicenses();
    }

    private void loadLicenses() {
        this.licenses.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_users_licenses ORDER BY id ASC")) {
            while (set.next()) {
                License license = null;
                if (!this.licenses.containsKey(set.getInt("id"))) {
                    license = new License(set);
                    this.licenses.put(set.getInt("id"), license);
                } else {
                    license = this.licenses.get(set.getInt("id"));
                    license.load(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void dispose() {
        this.licenses = null;
    }
}
