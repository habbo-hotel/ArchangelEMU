package com.eu.habbo.habbohotel.users;

import com.eu.habbo.habbohotel.navigation.DisplayMode;
import com.eu.habbo.habbohotel.navigation.ListMode;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HabboNavigatorPersonalDisplayMode {
    public ListMode listMode;
    public DisplayMode displayMode;

    public HabboNavigatorPersonalDisplayMode(ListMode listMode, DisplayMode collapsed) {
        this.listMode = listMode;
        this.displayMode = collapsed;
    }

    public HabboNavigatorPersonalDisplayMode(ResultSet set) throws SQLException {
        this.listMode = set.getBoolean("list_type") ? ListMode.THUMBNAILS : ListMode.LIST;
        this.displayMode = DisplayMode.valueOf(set.getString("display").toUpperCase());
    }
}
