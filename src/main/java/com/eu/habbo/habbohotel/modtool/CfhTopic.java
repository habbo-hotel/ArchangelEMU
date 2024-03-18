package com.eu.habbo.habbohotel.modtool;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class CfhTopic {
    private  final int id;
    private  final String name;
    private  final CfhActionType action;
    private  final boolean ignoreTarget;

    private  final String reply;
    private  final ModToolPreset defaultSanction;

    public CfhTopic(ResultSet set, ModToolPreset defaultSanction) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name_internal");
        this.action = CfhActionType.get(set.getString("action"));
        this.ignoreTarget = set.getString("ignore_target").equalsIgnoreCase("1");
        this.reply = set.getString("auto_reply");
        this.defaultSanction = defaultSanction;
    }
}