package com.eu.habbo.roleplay.facility.hospital;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.interactions.InteractionHospitalBed;
import com.eu.habbo.roleplay.room.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FacilityHospitalManager {

    private static FacilityHospitalManager instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(FacilityHospitalManager.class);

    public static FacilityHospitalManager getInstance() {
        if (instance == null) {
            instance = new FacilityHospitalManager();
        }
        return instance;
    }

    private FacilityHospitalManager() {
        long millis = System.currentTimeMillis();
        this.usersToHeal = new CopyOnWriteArrayList<>();
        LOGGER.info("Hospital Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    private final List<Habbo> usersToHeal;

    public Room getNearestHospital() {
        List<Room> hospitalRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithTag(RoomType.HOSPITAL);

        if (hospitalRooms.isEmpty()) {
            FacilityHospitalManager.LOGGER.error("No hospital rooms found");
            throw new RuntimeException("No hospital rooms found");
        }

        return hospitalRooms.get(0);
    }

    public void sendToHospital(Habbo habbo) {
        Room room = this.getNearestHospital();

        if (habbo.getRoomUnit().getRoom().getRoomInfo().getId() != room.getRoomInfo().getId()) {
            habbo.goToRoom(room.getRoomInfo().getId());
        }

        Collection<RoomItem> hospitalBedItems = room.getRoomItemManager().getItemsOfType(InteractionHospitalBed.class);
        for (RoomItem hospitalBedItem : hospitalBedItems) {
            List<RoomTile> hospitalBedRoomTiles = hospitalBedItem.getOccupyingTiles(room.getLayout());
            RoomTile firstAvailableHospitalBedTile = hospitalBedRoomTiles.get(0);
            if (firstAvailableHospitalBedTile == null) {
                return;
            }
            habbo.getRoomUnit().setLocation(firstAvailableHospitalBedTile);
        }
    }

    public void addUserToHeal(Habbo user) {
        this.usersToHeal.add(user);
        user.shout(Emulator.getTexts().getValue("roleplay.hospital.starts_healing"));
    }

    public void removeUserToHeal(Habbo user) {
        if (this.usersToHeal.isEmpty()) {
            return;
        }
        if (!user.getHabboRoleplayStats().isCuffed() && !user.getHabboRoleplayStats().isStunned()) {
            user.getRoomUnit().setCanWalk(true);
        }
        this.usersToHeal.remove(user);
        user.shout(Emulator.getTexts().getValue("roleplay.hospital.stops_healing"));
    }

    public void cycle() {
        for (Habbo user : usersToHeal) {
            if ((user.getHabboRoleplayStats().getHealthNow() + 1) > user.getHabboRoleplayStats().getHealthMax()) {
                this.removeUserToHeal(user);
                return;
            }
            user.getHabboRoleplayStats().setHealth(user.getHabboRoleplayStats().getHealthNow() + 1);
        }
    }

    public void dispose() {
    }
}