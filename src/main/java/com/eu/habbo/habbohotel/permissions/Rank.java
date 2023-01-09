package com.eu.habbo.habbohotel.permissions;

import gnu.trove.map.hash.THashMap;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Rank {

    @Getter
    private final int id;


    @Getter
    private final int level;
    @Getter
    private final THashMap<String, Permission> permissions;
    @Getter
    private final THashMap<String, String> variables;
    @Getter
    private String name;
    @Getter
    private String badge;
    @Getter
    private int roomEffect;


    @Getter
    private boolean logCommands;


    @Getter
    private String prefix;


    @Getter
    private String prefixColor;


    private boolean hasPrefix;
    @Getter
    private int diamondsTimerAmount;
    @Getter
    private int creditsTimerAmount;
    @Getter
    private int pixelsTimerAmount;
    @Getter
    private int gotwTimerAmount;

    public Rank(ResultSet set) throws SQLException {
        this.permissions = new THashMap<>();
        this.variables = new THashMap<>();
        this.id = set.getInt("id");
        this.level = set.getInt("level");
        this.diamondsTimerAmount = 1;
        this.creditsTimerAmount = 1;
        this.pixelsTimerAmount = 1;
        this.gotwTimerAmount = 1;

        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        this.name = set.getString("rank_name");
        this.badge = set.getString("badge");
        this.roomEffect = set.getInt("room_effect");
        this.logCommands = set.getString("log_commands").equals("1");
        this.prefix = set.getString("prefix");
        this.prefixColor = set.getString("prefix_color");
        this.diamondsTimerAmount = set.getInt("auto_points_amount");
        this.creditsTimerAmount = set.getInt("auto_credits_amount");
        this.pixelsTimerAmount = set.getInt("auto_pixels_amount");
        this.gotwTimerAmount = set.getInt("auto_gotw_amount");
        this.hasPrefix = !this.prefix.isEmpty();
        for (int i = 1; i < meta.getColumnCount() + 1; i++) {
            String columnName = meta.getColumnName(i);
            if (columnName.startsWith("cmd_") || columnName.startsWith("acc_")) {
                this.permissions.put(meta.getColumnName(i), new Permission(columnName, PermissionSetting.fromString(set.getString(i))));
            } else {
                this.variables.put(meta.getColumnName(i), set.getString(i));
            }
        }
    }

    public boolean hasPermission(String key, boolean isRoomOwner) {
        if (this.permissions.containsKey(key)) {
            Permission permission = this.permissions.get(key);

            return permission.getSetting() == PermissionSetting.ALLOWED || permission.getSetting() == PermissionSetting.ROOM_OWNER && isRoomOwner;

        }

        return false;
    }

    public boolean hasPrefix() {
        return this.hasPrefix;
    }

}

