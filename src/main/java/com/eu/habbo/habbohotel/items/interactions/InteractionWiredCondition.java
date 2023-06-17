package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredConditionOperator;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.messages.outgoing.wired.WiredConditionDataComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionWiredCondition extends InteractionWired {
    public InteractionWiredCondition(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionWiredCondition(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public abstract WiredConditionType getType();

    public WiredConditionOperator operator() {
        return WiredConditionOperator.AND;
    }
}
