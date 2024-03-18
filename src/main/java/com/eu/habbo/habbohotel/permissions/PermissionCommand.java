package com.eu.habbo.habbohotel.permissions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

@Slf4j
@Getter
public class PermissionCommand {
    private final String name;
    private final String description;
    private final String[] keys;

    public PermissionCommand(ResultSet set) throws SQLException {
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.keys = set.getString("keys").split(";");
    }

    public PermissionCommand(String name, String description, String[] keys) {
        this.name = name;
        this.description = description;
        this.keys = keys;
    }

    public String getDescription() {
        if(this.description == null || this.description.length() == 0) {
            return this.name + " - No description provided.";
        }

        return this.description;
    }

    public boolean hasKey(String key) {
        return Arrays.asList(this.keys).contains(key);
    }
}