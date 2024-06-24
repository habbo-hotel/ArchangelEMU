package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionOneWayGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.facility.prison.FacilityPrisonManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionNonPrisonerGate extends InteractionOneWayGate {
    public static String INTERACTION_TYPE = "rp_non_prisoner_gate";

    public InteractionNonPrisonerGate(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionNonPrisonerGate(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(final GameClient client, final Room room, Object[] objects) throws Exception {
        if (FacilityPrisonManager.getInstance().getPrisonTime(client.getHabbo()) != null) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.rp_non_prisoner_gate.not_allowed"));
            return;
        }

        super.onClick(client, room, objects);
    }
}