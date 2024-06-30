package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.actions.HospitalRecoveryAction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionHospitalBed extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_hospital_bed";

    public InteractionHospitalBed(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionHospitalBed(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
        Habbo habbo = room.getHabbosOnItem(roomUnit.getCurrentItem()).iterator().next();
        if (habbo == null) {
            return;
        }
        if (habbo.getHabboRoleplayStats().getHealthNow() >= habbo.getHabboRoleplayStats().getHealthMax()) {
            return;
        }
        Emulator.getThreading().run(new HospitalRecoveryAction(habbo));

    }
}