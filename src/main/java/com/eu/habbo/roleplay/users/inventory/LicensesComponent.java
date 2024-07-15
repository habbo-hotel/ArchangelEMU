package com.eu.habbo.roleplay.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.database.HabboLicenseRepository;
import com.eu.habbo.roleplay.corp.LicenseType;
import com.eu.habbo.roleplay.users.HabboLicense;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LicensesComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicensesComponent.class);

    @Getter
    private final THashMap<LicenseType, HabboLicense> licenses = new THashMap<LicenseType, HabboLicense>();

    public final Habbo habbo;
    public LicensesComponent(Habbo habbo) {
        this.habbo = habbo;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_users_licenses WHERE user_id = ?")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.licenses.put(LicenseType.fromValue(set.getInt("license_type")), HabboLicense.fromSet(set));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public HabboLicense getLicenseByType(LicenseType licenseType) {
        for (HabboLicense license : licenses.values()) {
            if (license.getLicenseType() == licenseType) {
                return license;
            }
        }
        return null;
    }

    public void createLicense(LicenseType licenseType) {
        HabboLicense license = HabboLicenseRepository.getInstance().create(licenseType, habbo.getHabboInfo().getId());
        this.licenses.put(licenseType, license);
    }

    public void dispose() {
        synchronized (this.licenses) {
            this.licenses.clear();
        }
    }

}