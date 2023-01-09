package com.eu.habbo.habbohotel.modtool;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
@Getter
public class ModToolPreset {
    private  final int id;
    private  final String name;
    private  final String message;
    private  final String reminder;
    private  final int banLength;
    private  final int muteLength;

    public ModToolPreset(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.message = set.getString("message");
        this.reminder = set.getString("reminder");
        this.banLength = set.getInt("ban_for");
        this.muteLength = set.getInt("mute_for");
    }
}
