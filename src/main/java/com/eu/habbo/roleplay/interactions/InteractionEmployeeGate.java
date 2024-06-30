package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionOneWayGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionEmployeeGate extends InteractionOneWayGate {
    public static String INTERACTION_TYPE = "rp_employee_gate";

    public InteractionEmployeeGate(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionEmployeeGate(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(final GameClient client, final Room room, Object[] objects) throws Exception {
        if (!client.getHabbo().getHabboRoleplayStats().isWorking()) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.employee_one_way_gate.must_be_working"));
            return;
        }

        if (client.getHabbo().getHabboRoleplayStats().getCorp().getGuild().getRoomId() != room.getRoomInfo().getId()) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.employee_one_way_gate.not_allowed"));
            return;
        }

        client.getHabbo().shout(Emulator.getTexts().getValue("roleplay.employee_one_way_gate.enter"));

        super.onClick(client, room, objects);
    }
}