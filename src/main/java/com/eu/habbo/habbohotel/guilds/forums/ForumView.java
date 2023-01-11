package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.database.DatabaseConstants;
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
        this.userId = set.getInt(DatabaseConstants.USER_ID);
        this.guildId = set.getInt("guild_id");
        this.timestamp = set.getInt("timestamp");
    }

}
