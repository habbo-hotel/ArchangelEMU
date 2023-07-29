package com.eu.habbo.habbohotel.items.interactions.totems;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTotemHead extends InteractionDefault {

    public InteractionTotemHead(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionTotemHead(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    public TotemType getTotemType() {
        int extraData;
        try {
            extraData = Integer.parseInt(this.getExtraData());
        } catch(NumberFormatException ex) {
            extraData = 0;
        }
        if(extraData < 3) {
            return TotemType.fromInt(extraData + 1);
        }
        return TotemType.fromInt((int)Math.ceil((extraData - 2) / 4.0f));
    }

    public TotemColor getTotemColor() {
        int extraData;
        try {
            extraData = Integer.parseInt(this.getExtraData());
        }catch(NumberFormatException ex) {
            extraData = 0;
        }
        if(extraData < 3) {
            return TotemColor.NONE;
        }
        return TotemColor.fromInt(extraData - 3 - (4 * (getTotemType().getType() - 1)));
    }

    private void update(Room room, RoomTile tile) {
        InteractionTotemLegs legs = null;

        for(RoomItem item : room.getRoomItemManager().getItemsAt(tile)) {
            if(item instanceof InteractionTotemLegs) {
                if (item.getCurrentZ() < this.getCurrentZ()) legs = (InteractionTotemLegs) item;
            }
        }

        if(legs == null)
            return;

        this.setExtraData(((4 * this.getTotemType().getType()) + legs.getTotemColor().getColor()) - 1 + "");
    }

    public void updateTotemState(Room room) {
        updateTotemState(room, room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()));
    }

    public void updateTotemState(Room room, RoomTile tile) {
        this.setExtraData(getTotemType().getType() - 1 + "");
        update(room, tile);
        this.needsUpdate(true);
        room.updateItem(this);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
        if (!((client != null && room != null && room.getRoomRightsManager().hasRights(client.getHabbo())) || (objects.length >= 2 && objects[1] instanceof WiredEffectType)))
            return;

        TotemType newType = TotemType.fromInt(getTotemType().getType() + 1);
        if(newType == TotemType.NONE) {
            newType = TotemType.TROLL;
        }

        this.setExtraData(newType.getType() - 1 + "");

        updateTotemState(room);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        updateTotemState(room, newLocation);
    }
}
