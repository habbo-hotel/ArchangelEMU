package com.eu.habbo.habbohotel.hotelview;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallOfFameWinner implements Comparable<HallOfFameWinner> {
    @Getter
    private final int id;
    @Getter
    private final String username;
    @Getter
    private final String look;
    @Getter
    private final int points;

    public HallOfFameWinner(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.username = set.getString("username");
        this.look = set.getString("look");
        this.points = set.getInt("hof_points");
    }


    @Override
    public int compareTo(HallOfFameWinner o) {
        return o.getPoints() - this.points;
    }
}
