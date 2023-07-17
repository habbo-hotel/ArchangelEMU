package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPyramid extends InteractionGate {
    private int nextChange;

    public InteractionPyramid(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPyramid(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public void change(Room room) {
        if (!(this.getExtradata().equals("0") || this.getExtradata().equals("1")))
            this.setExtradata("0");

        if (room != null) {
            RoomTile tile = room.getLayout().getTile(this.getX(), this.getY());

            if(tile == null) {
                return;
            }

            if (!room.getRoomUnitManager().hasHabbosAt(tile)) {
                int state = Integer.parseInt(this.getExtradata());
                state = Math.abs(state - 1);

                this.setExtradata(state + "");
                room.updateItemState(this);

                this.nextChange = Emulator.getIntUnixTimestamp() + 1 + (Emulator.getRandom().nextInt(Emulator.getConfig().getInt("pyramids.max.delay")));
            }
        }
    }

    public int getNextChange() {
        return this.nextChange;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}
