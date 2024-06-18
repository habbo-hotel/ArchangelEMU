package com.eu.habbo.roleplay.government;

import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class License {

    private int id;
    @Setter
    private int userID;
    private int licenseType;
    @Setter
    private int expiresOn;
    @Setter
    private int createdAt;
    @Setter
    private int updatedAt;

    public License(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.userID = set.getInt("user_id");
        this.licenseType = set.getInt("license_type");
        this.expiresOn = set.getInt("expires_on");
        this.createdAt = set.getInt("created_at");
        this.updatedAt = set.getInt("updated_at");
    }

}