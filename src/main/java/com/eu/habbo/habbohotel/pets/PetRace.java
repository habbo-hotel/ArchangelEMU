package com.eu.habbo.habbohotel.pets;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Getter
public class PetRace {
    private final int race;
    private final int colorOne;
    private final int colorTwo;
    private final boolean hasColorOne;
    private final boolean hasColorTwo;

    public PetRace(ResultSet set) throws SQLException {
        this.race = set.getInt("race");
        this.colorOne = set.getInt("color_one");
        this.colorTwo = set.getInt("color_two");
        this.hasColorOne = set.getString("has_color_one").equals("1");
        this.hasColorTwo = set.getString("has_color_two").equals("1");
    }
}
