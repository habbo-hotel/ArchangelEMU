package com.eu.habbo.habbohotel.guilds.forums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class ForumView {
    @Getter
    private final int userId;
    @Getter
    private final int guildId;
    @Getter
    private final int timestamp;

    public ForumView(ResultSet set) throws SQLException {
        this.userId = set.getInt("user_id");
        this.guildId = set.getInt("guild_id");
        this.timestamp = set.getInt("timestamp");
    }

}
