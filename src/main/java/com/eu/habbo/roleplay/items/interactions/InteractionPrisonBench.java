package com.eu.habbo.roleplay.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.room.FacilityHospitalManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPrisonBench extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_prison_bench";

    private Habbo habbo;


    public InteractionPrisonBench(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPrisonBench(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
        this.habbo = room.getHabbosOnItem(roomUnit.getCurrentItem()).iterator().next();
        if (this.habbo == null) {
            return;
        }
        if (this.habbo.getHabboRoleplayStats().getHealthNow() >= this.habbo.getHabboRoleplayStats().getHealthMax()) {
            return;
        }
        FacilityHospitalManager.getInstance().addUserToHeal(this.habbo);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
        if (this.habbo == null) {
            return;
        }
        FacilityHospitalManager.getInstance().removeUserToHeal(this.habbo);
    }
}