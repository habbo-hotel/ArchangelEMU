package com.eu.habbo.habbohotel.catalog;

import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClothItem {

    @Setter
    @Getter
    private int id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int[] setId;

    public ClothItem(ResultSet set) throws SQLException {
        this.setId(set.getInt("id"));
        this.setName(set.getString("name"));
        String[] parts = set.getString("setid").split(",");

        this.setSetId(new int[parts.length]);
        for (int i = 0; i < this.getSetId().length; i++) {
            this.getSetId()[i] = Integer.parseInt(parts[i]);
        }
    }

}
