package com.eu.habbo.roleplay.corporations;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;

@Getter
public class CorporationPosition {

    private int id;
    private int corporationID;
    @Setter
    private int roomID;
    @Setter
    private String name;
    @Setter
    private String description;
    @Setter
    private int salary;
    @Setter
    private String maleFigure;
    @Setter
    private String femaleFigure;

    public CorporationPosition(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.corporationID = set.getInt("corporation_id");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.salary = set.getInt("salary");
        this.maleFigure = set.getString("male_figure");
        this.femaleFigure = set.getString("female_figure");
    }



}