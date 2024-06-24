package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionOneWayGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTollGate extends InteractionOneWayGate {
    public static String INTERACTION_TYPE = "rp_toll_gate";

    public InteractionTollGate(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionTollGate(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(final GameClient client, final Room room, Object[] objects) throws Exception {
        int tollFee = 100;

        if (client.getHabbo().getHabboInfo().getCredits() < tollFee) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.toll_gate.not_enough"));
        }

        client.getHabbo().getHabboInfo().setCredits(client.getHabbo().getHabboInfo().getCredits() - tollFee);
        client.sendResponse(new CreditBalanceComposer((client.getHabbo())));
        client.sendResponse(new UserRoleplayStatsChangeComposer(client.getHabbo()));
        client.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.toll_gate.enter")
                .replace(":fee", String.valueOf(tollFee))
        );
        super.onClick(client, room, objects);
    }
}