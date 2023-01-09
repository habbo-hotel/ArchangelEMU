package com.eu.habbo.habbohotel.guilds;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class GuildPart {
    private final int id;

    private final String valueA;

    private final String valueB;

    public GuildPart(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.valueA = set.getString("firstvalue");
        this.valueB = set.getString("secondvalue");
    }
}
