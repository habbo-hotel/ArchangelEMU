package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPhone extends InteractionUsable {

    public static int PHONE_EFFECT_ID = 65;

    public static String INTERACTION_TYPE = "rp_phone";

    public InteractionPhone(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPhone(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

}