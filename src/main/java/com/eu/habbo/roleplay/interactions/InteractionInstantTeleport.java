package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.threading.runnables.teleport.TeleportAction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionInstantTeleport extends InteractionTeleport {

    public static String INTERACTION_TYPE = "rp_instant_teleport";

    public InteractionInstantTeleport(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionInstantTeleport(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        Habbo habbo = room.getHabbosOnItem(roomUnit.getCurrentItem()).iterator().next();

        if (habbo == null) {
            super.onWalkOn(roomUnit, room, objects);
            return;
        }

        if (habbo.getRoomUnit().getPreviousPosition() == null) {
            return;
        }

        habbo.getRoomUnit().setTeleporting(true);
        Emulator.getThreading().run(new TeleportAction(this, room, habbo.getClient()), 0);
    }
}