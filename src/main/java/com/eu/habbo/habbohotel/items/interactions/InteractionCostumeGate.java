package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.interfaces.ConditionalGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.CustomNotificationComposer;
import com.eu.habbo.threading.runnables.CloseGate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class InteractionCostumeGate extends InteractionDefault implements ConditionalGate {
    public InteractionCostumeGate(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
    }

    public InteractionCostumeGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        if (roomUnit == null || room == null)
            return false;

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null && habbo.getHabboInfo() != null) {
            /*
             * Get all figureparts. Figureparts are seperated by dots and each figurepart has this format:
             * figureType-partID-colorID1-colorID2...-colorIDn
             */
            List<String> figureParts = Arrays.asList(habbo.getHabboInfo().getLook().split("\\."));

            List<String> allowedPartIds = Arrays.asList(Emulator.getConfig()
                    .getValue("hotel.item.condition.costume.partids")
                    .split(";")
            );

            // Check if at least one of the figureparts is configured as a costume and thus allowed
            return figureParts.stream().anyMatch(figurePart -> {
                String[] partInfo = figurePart.split("-");
                if (partInfo.length >= 2) {
                    String partID = partInfo[1]; // index 0 is the part, index 1 is the ID
                    return allowedPartIds.contains(partID);
                }
                return false;
            });
        }
        return false;
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        if (this.canWalkOn(roomUnit, room, objects)) {
            this.setExtradata("1");
            room.updateItemState(this);
        }
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (client != null) {
            if (this.canWalkOn(client.getHabbo().getRoomUnit(), room, null)) {
                super.onClick(client, room, objects);
            } else {
                client.sendResponse(new CustomNotificationComposer(CustomNotificationComposer.GATE_NO_HC));
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);

        Emulator.getThreading().run(new CloseGate(this, room), 1000);
    }

    @Override
    public void onRejected(RoomUnit roomUnit, Room room, Object[] objects) {
        if (roomUnit == null || room == null)
            return;

        room.getHabbo(roomUnit).getClient().sendResponse(
                new CustomNotificationComposer(CustomNotificationComposer.HOPPER_NO_COSTUME)
        );
    }
}
