package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionHanditem extends InteractionDefault {
    public InteractionHanditem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionHanditem(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (RoomLayout.tilesAdjecent(client.getHabbo().getRoomUnit().getCurrentPosition(), room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY())) ||
                (client.getHabbo().getRoomUnit().getCurrentPosition().getX() == this.getCurrentPosition().getX() && client.getHabbo().getRoomUnit().getCurrentPosition().getY() == this.getCurrentPosition().getY())) {
            this.handle(room, client.getHabbo().getRoomUnit());
        }
    }

    protected void handle(Room room, RoomUnit roomUnit) {
        if (this.getExtraData().isEmpty()) this.setExtraData("0");

        if (!this.getExtraData().equals("0")) return;

        if(!(roomUnit instanceof RoomAvatar roomAvatar)) {
            return;
        }

        RoomItem instance = this;
        roomAvatar.setHandItem(this.getBaseItem().getRandomVendingItem());
        room.sendComposer(new CarryObjectMessageComposer(roomAvatar).compose());

        if (this.getBaseItem().getStateCount() > 1) {
            this.setExtraData("1");
            room.updateItem(this);

            Emulator.getThreading().run(() -> {
                InteractionHanditem.this.setExtraData("0");
                room.updateItem(instance);
            }, 500);
        }
    }
}