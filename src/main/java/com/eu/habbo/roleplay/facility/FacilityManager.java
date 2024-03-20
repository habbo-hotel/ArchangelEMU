package com.eu.habbo.roleplay.facility;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import lombok.Getter;

import java.util.List;

public class FacilityManager {
    private static FacilityManager instance;

    public static FacilityManager getInstance() {
        if (instance == null) {
            instance = new FacilityManager();
        }
        return instance;
    }

    @Getter
    private final FacilityHospitalsManager facilityHospitalsManager;

    private FacilityManager() {
        this.facilityHospitalsManager =FacilityHospitalsManager.getInstance();
        this.facilityHospitalsManager.startHealingProcess();
    }

    public static Room getFirstRoomWithTag(String tag) {
        List<Room> matchingRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithTag(tag);
        if (matchingRooms.isEmpty()) {
            return null;
        }
        return matchingRooms.get(0);
    }

}
