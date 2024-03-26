package com.eu.habbo.roleplay.gangs;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;

@Getter
public class GangPosition {

    private int id;
    private int gangID;
    @Setter
    private int roomID;
    @Setter
    private String name;
    @Setter
    private String description;

    public GangPosition(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.gangID = set.getInt("gang_id");
        this.name = set.getString("name");
        this.description = set.getString("description");
    }



}