package com.eu.habbo.habbohotel.permissions;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionRight {
    @Getter
    private final String name;

    private final String description;

    public PermissionRight(ResultSet set) throws SQLException {
        this.name = set.getString("name");
        this.description = set.getString("description");
    }

    public String getDescription() {
        if(this.description == null || this.description.length() == 0) {
            return this.name + " - No description provided.";
        }

        return this.description;
    }
}
