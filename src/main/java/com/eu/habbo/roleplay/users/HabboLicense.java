package com.eu.habbo.roleplay.users;

import com.eu.habbo.roleplay.corp.LicenseType;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HabboLicense {

    public static HabboLicense fromSet(ResultSet set) throws SQLException {
        return new HabboLicense(
                set.getInt("id"),
                set.getInt("user_id"),
                LicenseType.fromValue(set.getInt("license_type")),
                set.getLong("expires_on"),
                set.getLong("created_at"),
                set.getLong("updated_at")
        );
    }

    @Getter
    private int id;
    @Getter
    private int userID;
    @Getter
    private LicenseType licenseType;
    @Getter
    private long expiresOn;
    @Getter
    private long createdAt;
    @Getter
    private long updatedAt;

    public HabboLicense(int id, int userID, LicenseType licenseType, long expiresOn, long createdAt, long updatedAt) {
        this.id = id;
        this.userID = userID;
        this.licenseType = licenseType;
        this.expiresOn = expiresOn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
