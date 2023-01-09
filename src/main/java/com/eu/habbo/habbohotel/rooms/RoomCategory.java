package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.navigation.ListMode;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class RoomCategory implements Comparable<RoomCategory> {

    private final int id;
    private final int minRank;
    private final String caption;
    private final String captionSave;
    private final boolean canTrade;
    private final int maxUserCount;
    private final boolean isPublic;
    private final ListMode displayMode;

    private final int order;

    public RoomCategory(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.minRank = set.getInt("min_rank");
        this.caption = set.getString("caption");
        this.captionSave = set.getString("caption_save");
        this.canTrade = set.getBoolean("can_trade");
        this.maxUserCount = set.getInt("max_user_count");
        this.isPublic = set.getString("public").equals("1");
        this.displayMode = ListMode.fromType(set.getInt("list_type"));
        this.order = set.getInt("order_num");
    }

    @Override
    public int compareTo(RoomCategory o) {
        return o.getId() - this.getId();
    }
}
