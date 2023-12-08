package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionCrackableMaster extends InteractionCrackable {
    public InteractionCrackableMaster(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionCrackableMaster(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    protected boolean placeInRoom() {
        return false;
    }

    @Override
    public boolean resetable() {
        return true;
    }

    @Override
    public boolean allowAnyone() {
        return true;
    }
}
