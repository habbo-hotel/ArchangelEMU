package com.eu.habbo.habbohotel.achievements;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class AchievementLevel {

    private final int level;
    private final int rewardAmount;
    private final int rewardType;

    private final int points;

    private final int progress;

    public AchievementLevel(ResultSet set) throws SQLException {
        this.level = set.getInt("level");
        this.rewardAmount = set.getInt("reward_amount");
        this.rewardType = set.getInt("reward_type");
        this.points = set.getInt("points");
        this.progress = set.getInt("progress_needed");
    }
}
