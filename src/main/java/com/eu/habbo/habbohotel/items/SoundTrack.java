package com.eu.habbo.habbohotel.items;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class SoundTrack {
    private final int id;
    private final String name;
    private final String author;
    private final String code;
    private final String data;
    private final int length;

    public SoundTrack(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.author = set.getString("author");
        this.code = set.getString("code");
        this.data = set.getString("track");
        this.length = set.getInt("length");
    }


}
