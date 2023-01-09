package com.eu.habbo.habbohotel.users;

import com.eu.habbo.habbohotel.navigation.DisplayMode;
import com.eu.habbo.habbohotel.navigation.ListMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@AllArgsConstructor
public class HabboNavigatorPersonalDisplayMode {
    private ListMode listMode;
    private DisplayMode displayMode;

    public HabboNavigatorPersonalDisplayMode(ResultSet set) throws SQLException {
        this.listMode = set.getString("list_type").equals("thumbnails") ? ListMode.THUMBNAILS : ListMode.LIST;
        this.displayMode = DisplayMode.valueOf(set.getString("display").toUpperCase());
    }
}
