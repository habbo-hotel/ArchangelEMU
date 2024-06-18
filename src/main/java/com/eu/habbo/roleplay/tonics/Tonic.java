package com.eu.habbo.roleplay.tonics;

import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class Tonic  {

    private int id;

    @Setter
    private String displayName;
    private String uniqueName;
    @Setter
    private String type;
    @Setter
    private int healthDiff;
    @Setter
    private int energyDiff;
    @Setter
    private int weight;
    @Setter
    private int consumeEffect;
    @Setter
    private String consumeMessage;

    public Tonic(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.displayName = set.getString("display_name");
        this.uniqueName = set.getString("unique_name");
        this.type = set.getString("type");
        this.healthDiff = set.getInt("health_diff");
        this.energyDiff = set.getInt("energy_diff");
        this.weight = set.getInt("weight");
        this.consumeEffect = set.getInt("consume_effect");
        this.consumeMessage = set.getString("consume_message");
    }

}