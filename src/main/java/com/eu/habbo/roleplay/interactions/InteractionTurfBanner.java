package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionGuildFurni;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTurfBanner extends InteractionGuildFurni {

    public static String INTERACTION_TYPE = "rp_turf_banner";

    public InteractionTurfBanner(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionTurfBanner(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

}
