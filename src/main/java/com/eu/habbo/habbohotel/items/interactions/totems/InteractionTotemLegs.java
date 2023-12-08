package com.eu.habbo.habbohotel.items.interactions.totems;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTotemLegs extends InteractionDefault {
    public InteractionTotemLegs(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionTotemLegs(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    public TotemType getTotemType() {
        int extraData;
        try {
            extraData = Integer.parseInt(this.getExtraData());
        } catch(NumberFormatException ex) {
            extraData = 0;
        }
        return TotemType.fromInt((int)Math.ceil((extraData + 1) / 4.0f));
    }

    public TotemColor getTotemColor() {
        int extraData;
        try {
            extraData = Integer.parseInt(this.getExtraData());
        } catch(NumberFormatException ex) {
            extraData = 0;
        }
        return TotemColor.fromInt(extraData - (4 * (getTotemType().getType() - 1)));
    }

    private void updateHead(Room room, RoomTile tile) {
        for(RoomItem item : room.getRoomItemManager().getItemsAt(tile)) {
            if(item instanceof InteractionTotemHead) {
                if (item.getCurrentZ() > this.getCurrentZ()) ((InteractionTotemHead) item).updateTotemState(room);
            }
        }
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (room == null || !((client != null && room.getRoomRightsManager().hasRights(client.getHabbo())) || (objects.length >= 2 && objects[1] instanceof WiredEffectType)))
            return;

        updateHead(room, room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()));
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);

        updateHead(room, oldLocation);
    }
}
