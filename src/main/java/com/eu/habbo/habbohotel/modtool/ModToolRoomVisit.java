package com.eu.habbo.habbohotel.modtool;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;


@Getter
public class ModToolRoomVisit implements Comparable<ModToolRoomVisit> {
    private final int roomId;
    private final String roomName;
    private final int timestamp;
    private int exitTimestamp;
    private final THashSet<ModToolChatLog> chat = new THashSet<>();

    public ModToolRoomVisit(ResultSet set) throws SQLException {
        this.roomId = set.getInt("room_id");
        this.roomName = set.getString("name");
        this.timestamp = set.getInt("timestamp");
    }

    public ModToolRoomVisit(int roomId, String roomName, int timestamp, int exitTimestamp) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.timestamp = timestamp;
        this.exitTimestamp = exitTimestamp;

    }

    @Override
    public int compareTo(ModToolRoomVisit o) {
        return o.timestamp - this.timestamp;
    }
}
