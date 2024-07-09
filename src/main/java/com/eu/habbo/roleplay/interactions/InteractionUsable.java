package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionUsable extends InteractionDefault {
    public static String INTERACTION_TYPE = "rp_usable";

    public InteractionUsable(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionUsable(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

}