package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.outgoing.generic.alerts.CustomUserNotificationMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionCostumeHopper extends InteractionHopper {
    public InteractionCostumeHopper(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionCostumeHopper(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (client.getHabbo().getRoomUnit().getEffectId() > 0) {
            super.onClick(client, room, objects);
        } else {
            client.sendResponse(new CustomUserNotificationMessageComposer(CustomUserNotificationMessageComposer.HOPPER_NO_COSTUME));
        }
    }
}
