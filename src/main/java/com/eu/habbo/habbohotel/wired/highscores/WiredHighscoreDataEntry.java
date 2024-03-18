package com.eu.habbo.habbohotel.wired.highscores;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class WiredHighscoreDataEntry {
    private final int itemId;
    private final List<Integer> userIds;
    private final int score;
    private final boolean isWin;
    private final int timestamp;


    public WiredHighscoreDataEntry(ResultSet set) throws SQLException {
        this.itemId = set.getInt("item_id");
        this.userIds = Arrays.stream(set.getString("user_ids").split(",")).map(Integer::valueOf).collect(Collectors.toList());
        this.score = set.getInt("score");
        this.isWin = set.getInt("is_win") == 1;
        this.timestamp = set.getInt("timestamp");
    }
}
