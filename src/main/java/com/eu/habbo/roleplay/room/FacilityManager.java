package com.eu.habbo.roleplay.room;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import lombok.Getter;

import java.util.List;

@Getter
public class FacilityManager {
    private static FacilityManager instance;

    public static FacilityManager getInstance() {
        if (instance == null) {
            instance = new FacilityManager();
        }
        return instance;
    }

    private final FacilityHospitalManager facilityHospitalManager;

    private  final FacilityPrisonManager facilityPrisonManager;

    private FacilityManager() {
        this.facilityHospitalManager = FacilityHospitalManager.getInstance();
        this.facilityHospitalManager.cycle();

        this.facilityPrisonManager = FacilityPrisonManager.getInstance();
        this.facilityPrisonManager.cycle();
    }

    public void cycle(Room room) {
        if (room.getRoomInfo().getId() == FacilityHospitalManager.getInstance().getHospital().getRoomInfo().getId()) {
            FacilityHospitalManager.getInstance().cycle();
        }

        if (room.getRoomInfo().getId() == FacilityPrisonManager.getInstance().getPrison().getRoomInfo().getId()) {
            FacilityPrisonManager.getInstance().cycle();
        }
    }

    public static Room getFirstRoomWithTag(String tag) {
        List<Room> matchingRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithTag(tag);
        if (matchingRooms.isEmpty()) {
            return null;
        }
        return matchingRooms.get(0);
    }

}
