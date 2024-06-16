package com.eu.habbo.roleplay.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.messages.outgoing.corp.CashRegisterComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionCashRegister extends InteractionDefault {

    public static String CASH_REGISTER_INTERACTION_TYPE = "rp_cash_register";

    public InteractionCashRegister(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionCashRegister(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        client.sendResponse(new CashRegisterComposer(client.getHabbo()));
    }
}