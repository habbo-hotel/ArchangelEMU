package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.messages.outgoing.corp.CashRegisterComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBankComputer extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_bank_pc";

    public InteractionBankComputer(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionBankComputer(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        client.sendResponse(new CashRegisterComposer(client.getHabbo()));
    }
}