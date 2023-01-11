package com.eu.habbo.habbohotel.campaign;

import com.eu.habbo.database.DatabaseConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class CalendarRewardClaimed {
    private final int userId;
    private final int campaignId;
    private final int day;
    private final int rewardId;
    private final Timestamp timestamp;

    public CalendarRewardClaimed(ResultSet set) throws SQLException {
        this.userId = set.getInt(DatabaseConstants.USER_ID);
        this.campaignId = set.getInt("campaign_id");
        this.day = set.getInt("day");
        this.rewardId = set.getInt("reward_id");
        this.timestamp = new Timestamp(set.getInt("timestamp") * 1000L);
    }
}
