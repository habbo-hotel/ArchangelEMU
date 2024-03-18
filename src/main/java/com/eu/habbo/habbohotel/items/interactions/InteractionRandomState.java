package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.RandomStateParams;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionRandomState extends InteractionDefault {
    public InteractionRandomState(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionRandomState(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);

        this.setExtraData("");
        room.updateItemState(this);
    }

    public void onRandomStateClick(Room room) throws Exception {
        RandomStateParams params = new RandomStateParams(this.getBaseItem().getCustomParams());

        this.setExtraData("");
        room.updateItemState(this);

        int randomState = Emulator.getRandom().nextInt(params.getStates()) + 1;

        Emulator.getThreading().run(() -> {
            this.setExtraData(randomState + "");
            room.updateItemState(this);
        }, params.getDelay());
    }
}
