package com.eu.habbo.roleplay.weapons;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Weapon {
    private int id;
    private String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String newDisplayName) {
        this.displayName = newDisplayName;
    }

    private String uniqueName;

    public String getUniqueName() {
        return this.uniqueName;
    }

    public void setUniqueName(String newUniqueName) {
        this.uniqueName = newUniqueName;
    }

    private String type;

    public String getType() {
        return this.type;
    }

    public void setType(String newType) {
        this.type = newType;
    }
    private int minDamage;

    public int getMinDamage() {
        return this.minDamage;
    }

    public void setMinDamage(int newMinDamage) {
        this.minDamage = newMinDamage;
    }

    private int maxDamage;

    public int getMaxDamage() {
        return this.maxDamage;
    }

    public void setMaxDamage(int newMaxDamage) {
        this.maxDamage = newMaxDamage;
    }

    private int rangeInTiles;

    public int getRangeInTiles() {
        return this.rangeInTiles;
    }

    public void setRangeInTiles(int newRange) {
        this.rangeInTiles = newRange;
    }

    private double accuracy;

    public double getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(double newAccuracy) {
        this.accuracy = newAccuracy;
    }

    private double reloadTime;

    public double getReloadTime() {
        return this.reloadTime;
    }

    public void setReloadTime(double newReloadTime) {
        this.reloadTime = newReloadTime;
    }

    private int ammoCapacity;

    public int getAmmoCapacity() {
        return this.ammoCapacity;
    }

    public void setAmmoCapacity(int newAmmoCapacity) {
        this.ammoCapacity = newAmmoCapacity;
    }

    private double weight;

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double newWeight) {
        this.weight = newWeight;
    }

    private int cooldownSeconds;

    public int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public void setCooldownSeconds(int newCooldownSeconds) {
        this.cooldownSeconds = newCooldownSeconds;
    }

    private String specialAbilities;

    public String[] getSpecialAbilities() {
        return specialAbilities.split(",");
    }

    public void setSpecialAbilities(String[] newSpecialAbilities) {
        this.specialAbilities = Arrays.toString(newSpecialAbilities);
    }

    private String equipEffect;

    public String getEquipEffect() {
        return this.equipEffect;
    }

    public void setEquipEffect(String newEquipEffect) {
        this.equipEffect = newEquipEffect;
    }

    private String equipMessage;

    public String getEquipMessage() {
        return this.equipMessage;
    }

    public void setEquipMessage(String newEquipMessage) {
        this.equipMessage = newEquipMessage;
    }

    private String attackMessage;

    public String getAttackMessage() {
        return this.attackMessage;
    }

    public void setAttackMessage(String newAttackMessage) {
        this.attackMessage = newAttackMessage;
    }

    public Weapon(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.displayName = set.getString("display_name");
        this.uniqueName = set.getString("unique_name");
        this.type = set.getString("type");
        this.minDamage = set.getInt("damage_min");
        this.maxDamage = set.getInt("damage_max");
        this.rangeInTiles = set.getInt("range_in_tiles");
        this.accuracy = set.getInt("accuracy");
        this.reloadTime = set.getInt("reload_time");
        this.ammoCapacity = set.getInt("capacity");
        this.weight = set.getInt("weight");
        this.cooldownSeconds = set.getInt("cooldown_secs");
        this.specialAbilities = set.getString("special_abilities");
        this.equipEffect = set.getString("equip_effect");
        this.equipMessage = set.getString("equip_message");
        this.attackMessage = set.getString("attack_message");
    }




}