package com.eu.habbo.roleplay.gangs;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;

@Getter
public class GangPosition {

    private int id;
    private int gangID;
    @Getter
    @Setter
    private int orderID;
    @Setter
    private String name;
    @Setter
    private String description;
    @Getter
    @Setter
    private boolean canKick;
    @Getter
    @Setter
    private boolean canInvite;

    public GangPosition(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.gangID = set.getInt("gang_id");
        this.orderID = set.getInt("order_id");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.canKick = set.getInt("can_kick") == 1;
        this.canInvite = set.getInt("can_invite") == 1;
    }



}