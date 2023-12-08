package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class InteractionDefault extends RoomItem {
    public InteractionDefault(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionDefault(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean isWalkable() {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);

        //Check if there a no rollers
        if (room.getRoomItemManager().getItemsAt(oldLocation).stream().noneMatch(item -> item.getClass().isAssignableFrom(InteractionRoller.class))) {
            for (RoomUnit unit : room.getRoomUnitManager().getCurrentRoomUnits().values()) {
                if (!oldLocation.unitIsOnFurniOnTile(unit, this.getBaseItem())) {
                    continue; // If the unit was previously on the furni...
                }

                if (newLocation.unitIsOnFurniOnTile(unit, this.getBaseItem())) {
                    continue; // but is not anymore...
                }

                try {
                    this.onWalkOff(unit, room, new Object[]{oldLocation, newLocation}); // the unit walked off!
                } catch (Exception ignored) {

                }
            }
        }
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (room != null && (client == null || this.canToggle(client.getHabbo(), room) || (objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE))) {
            super.onClick(client, room, objects);

            if (objects != null && objects.length > 0) {
                if (objects[0] instanceof Integer) {
                    if (this.getExtraData().length() == 0)
                        this.setExtraData("0");

                    if (this.getBaseItem().getStateCount() > 0) {
                        int currentState = 0;

                        try {
                            currentState = Integer.parseInt(this.getExtraData());
                        } catch (NumberFormatException e) {
                            log.error("Incorrect extradata (" + this.getExtraData() + ") for item ID (" + this.getId() + ") of type (" + this.getBaseItem().getName() + ")");
                        }

                        this.setExtraData("" + (currentState + 1) % this.getBaseItem().getStateCount());
                        this.setSqlUpdateNeeded(true);

                        room.updateItemState(this);
                    }
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
    }

    public boolean canToggle(Habbo habbo, Room room) {
        if (room.getRoomRightsManager().hasRights(habbo)) return true;

        if (!habbo.getHabboStats().isRentingSpace()) return false;

        RoomItem rentSpace = room.getRoomItemManager().getRoomItemById(habbo.getHabboStats().getRentedItemId());

        return rentSpace != null && RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getCurrentPosition().getX(), rentSpace.getCurrentPosition().getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(this.getCurrentPosition().getX(), this.getCurrentPosition().getY(), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()));

    }

    @Override
    public boolean allowWiredResetState() {
        return true;
    }
}
