package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.messages.outgoing.corp.CorpOpenInventoryComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionCorpInventory extends InteractionDefault {

    public static String INTERACTION_TYPE = "rp_corp_inventory";

    public InteractionCorpInventory(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionCorpInventory(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        Corp corp = client.getHabbo().getHabboRoleplayStats().getCorp();

        if (corp == null) {
            client.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.corp_not_found"));
            return;
        }

        client.sendResponse(new CorpOpenInventoryComposer(this, corp));
    }

}