package com.eu.habbo.habbohotel.permissions;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionRight {
    @Getter
    private String name;
    @Getter
    private String description;

    public PermissionRight(ResultSet set) throws SQLException {
        this.name = set.getString("name");
        this.description = set.getString("description");
    }
}
