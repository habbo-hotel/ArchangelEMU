package com.eu.habbo.roleplay.facility;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import java.util.List;

public class FacilityManager {
    private FacilityManager() { }

    public static Room getFirstRoomWithTag(String tag) {
        List<Room> matchingRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithTag(tag);
        if (matchingRooms.isEmpty()) {
            return null;
        }
        return matchingRooms.get(0);
    }

}
