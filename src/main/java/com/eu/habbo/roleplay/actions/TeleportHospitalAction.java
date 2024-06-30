package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.list.LayCommand;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.interactions.InteractionHospitalBed;
import com.eu.habbo.roleplay.room.RoomType;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class TeleportHospitalAction implements Runnable {

    private final Habbo habbo;

    @Override
    public void run() {
        if (this.habbo.getRoomUnit() == null) {
            return;
        }

        if (this.habbo.getRoomUnit().getRoom().getRoomInfo().getTags().contains(RoomType.HOSPITAL)) {
            return;
        }

        if (this.habbo.getHabboRoleplayStats().getIsEscorting() != null) {
            this.habbo.getHabboRoleplayStats().setIsEscorting(null);
        }

        this.habbo.shout(Emulator.getTexts().getValue("roleplay.user_is_dead"));
        this.habbo.getRoomUnit().setCanWalk(false);

        int deadTeleportDelay = Emulator.getConfig().getInt("roleplay.dead.delay", 10000);

        new LayCommand().handle(this.habbo.getClient(), new String[0]);

        this.habbo.shout(Emulator.getTexts().getValue("roleplay.dead.teleporting_to_hospital_delay").replace(":seconds", String.valueOf(deadTeleportDelay / 1000)));


        List<Room> hospitalRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsByTag(RoomType.HOSPITAL);

        if (hospitalRooms.isEmpty()) {
            throw new RuntimeException("no hospitals found");
        }

        Room room = hospitalRooms.get(0);

        habbo.goToRoom(room.getRoomInfo().getId());

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
}
