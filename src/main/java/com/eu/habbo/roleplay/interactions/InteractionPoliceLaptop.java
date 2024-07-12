package com.eu.habbo.roleplay.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPoliceLaptop extends InteractionUsable {

    public static int LAPTOP_EFFECT_ID = 65;

    public static String INTERACTION_TYPE = "rp_police_laptop";

    public InteractionPoliceLaptop(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPoliceLaptop(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

}