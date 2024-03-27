package com.eu.habbo.roleplay.corporations;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;

@Getter
public class CorporationPosition implements Runnable {

    private int id;
    private int corporationID;
    @Getter
    @Setter
    private int orderID;
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
    @Getter
    @Setter
    private boolean canHire;
    @Getter
    @Setter
    private boolean canFire;
    @Getter
    @Setter
    private boolean canPromote;
    @Getter
    @Setter
    private boolean canDemote;

    public CorporationPosition(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.corporationID = set.getInt("corporation_id");
        this.orderID = set.getInt("order_id");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.salary = set.getInt("salary");
        this.maleFigure = set.getString("male_figure");
        this.femaleFigure = set.getString("female_figure");
        this.canHire = set.getInt("can_hire") == 1;
        this.canFire = set.getInt("can_fire") == 1;
        this.canPromote = set.getInt("can_promote") == 1;
        this.canDemote = set.getInt("can_demote") == 1;
    }

    @Override
    public void run() {
        CorporationPositionRepository.getInstance().upsertCorporationPosition(this);
    }

}