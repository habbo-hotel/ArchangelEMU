package com.eu.habbo.roleplay.interactions;


import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.messages.outgoing.device.InfoScreenViewComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionInfoScreen extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_info_screen";

    public InteractionInfoScreen(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionInfoScreen(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
        client.sendResponse(new InfoScreenViewComposer(this));
    }
}
