package com.eu.habbo.roleplay.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.messages.outgoing.user.UserWardrobeOpenComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionWardrobe extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_wardrobe";

    private Habbo habbo;


    public InteractionWardrobe(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionWardrobe(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
        this.habbo = room.getHabbosOnItem(roomUnit.getCurrentItem()).iterator().next();
        this.habbo.getClient().sendResponse(new UserWardrobeOpenComposer());

    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
        if (this.habbo == null) {
            return;
        }
    }
}