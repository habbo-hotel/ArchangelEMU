package com.eu.habbo.habbohotel.campaign;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class CalendarCampaign {
    private int id;
    private final String name;
    private final String image;
    private Map<Integer , CalendarRewardObject> rewards = new THashMap<>();
    private final Integer startTimestamp;
    private final int totalDays;
    private final boolean lockExpired;

    public CalendarCampaign(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.image = set.getString("image");
        this.startTimestamp = set.getInt("start_timestamp");
        this.totalDays = set.getInt("total_days");
        this.lockExpired = set.getInt("lock_expired") == 1;
    }

    public CalendarCampaign(int id, String name, String image, Integer startTimestamp, int totalDays, boolean lockExpired) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.startTimestamp = startTimestamp;
        this.totalDays = totalDays;
        this.lockExpired = lockExpired;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getImage() {
        return this.image;
    }

    public Integer getStartTimestamp() {
        return this.startTimestamp;
    }

    public int getTotalDays() { return this.totalDays; }

    public boolean getLockExpired() { return this.lockExpired; }

    public Map<Integer, CalendarRewardObject> getRewards() { return rewards; }

    public void setId(int id) { this.id = id; }

    public void setRewards(Map<Integer, CalendarRewardObject> rewards) { this.rewards = rewards; }

    public void addReward(CalendarRewardObject reward) { this.rewards.put(reward.getId(), reward); }
}
