package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.RedeemableSubscriptionType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.users.UserClubComposer;
import com.eu.habbo.messages.outgoing.users.UserPermissionsComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionRedeemableSubscriptionBox extends InteractionCrackable {
    public InteractionRedeemableSubscriptionBox(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionRedeemableSubscriptionBox(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public boolean userRequiredToBeAdjacent() {
        return false;
    }
}
