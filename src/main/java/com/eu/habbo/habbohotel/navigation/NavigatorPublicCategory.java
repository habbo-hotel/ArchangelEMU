package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.habbohotel.rooms.Room;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class NavigatorPublicCategory {
    private final int id;
    private final String name;
    private final List<Room> rooms;
    private final ListMode image;
    private final int order;

    public NavigatorPublicCategory(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.image = set.getString("image").equals("1") ? ListMode.THUMBNAILS : ListMode.LIST;
        this.order = set.getInt("order_num");
        this.rooms = new ArrayList<>();
    }

    public void addRoom(Room room) {
        room.preventUncaching = true;
        this.rooms.add(room);
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room);
        room.preventUncaching = room.isPublicRoom();
    }
}