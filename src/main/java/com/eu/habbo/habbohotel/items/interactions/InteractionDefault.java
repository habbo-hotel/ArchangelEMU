package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomBot;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
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

    public InteractionDefault(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

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

        if (room.getItemsAt(oldLocation).stream().noneMatch(item -> item.getClass().isAssignableFrom(InteractionRoller.class))) {
            for (RoomUnit unit : room.getRoomUnitManager().getCurrentRoomUnits().values()) {
                if (!oldLocation.unitIsOnFurniOnTile(unit, this.getBaseItem()))
                    continue; // If the unit was previously on the furni...
                if (newLocation.unitIsOnFurniOnTile(unit, this.getBaseItem())) continue; // but is not anymore...

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
                    if (this.getExtradata().length() == 0)
                        this.setExtradata("0");

                    if (this.getBaseItem().getStateCount() > 0) {
                        int currentState = 0;

                        try {
                            currentState = Integer.parseInt(this.getExtradata());
                        } catch (NumberFormatException e) {
                            log.error("Incorrect extradata (" + this.getExtradata() + ") for item ID (" + this.getId() + ") of type (" + this.getBaseItem().getName() + ")");
                        }

                        this.setExtradata("" + (currentState + 1) % this.getBaseItem().getStateCount());
                        this.needsUpdate(true);

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

        if (roomUnit == null || (this.getBaseItem().getEffectF() == 0 && this.getBaseItem().getEffectM() == 0)) {
            return;
        }

        if (roomUnit instanceof RoomHabbo roomHabbo) {
            Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomHabbo);

            if (habbo == null) return;

            if (habbo.getHabboInfo().getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0 && roomHabbo.getEffectId() != this.getBaseItem().getEffectM()) {
                if (roomHabbo.getEffectId() > 0) {
                    roomHabbo.setPreviousEffectId(roomHabbo.getEffectId(), roomHabbo.getPreviousEffectEndTimestamp());
                }

                roomHabbo.giveEffect(this.getBaseItem().getEffectM(), -1);
                return;
            }

            if (habbo.getHabboInfo().getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0 && roomHabbo.getEffectId() != this.getBaseItem().getEffectF()) {
                if (roomHabbo.getEffectId() > 0) {
                    roomHabbo.setPreviousEffectId(roomHabbo.getEffectId(), roomHabbo.getPreviousEffectEndTimestamp());
                }
                roomHabbo.giveEffect(this.getBaseItem().getEffectF(), -1);
            }
        } else if (roomUnit instanceof RoomBot roomBot) {
            Bot bot = room.getRoomUnitManager().getBotByRoomUnit(roomBot);

            if (bot == null) return;

            if (bot.getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0 && roomBot.getEffectId() != this.getBaseItem().getEffectM()) {
                if (roomBot.getEffectId() > 0) {
                    roomBot.setPreviousEffectId(roomBot.getEffectId(), roomBot.getPreviousEffectEndTimestamp());
                }

                roomBot.giveEffect(this.getBaseItem().getEffectM(), -1);
                return;
            }

            if (bot.getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0 && roomBot.getEffectId() != this.getBaseItem().getEffectF()) {
                if (roomBot.getEffectId() > 0) {
                    roomUnit.setPreviousEffectId(roomBot.getEffectId(), roomBot.getPreviousEffectEndTimestamp());
                }

                roomBot.giveEffect(this.getBaseItem().getEffectF(), -1);
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);

        if (roomUnit != null) {
            if (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0) {
                int nextEffectM = 0;
                int nextEffectF = 0;
                int nextEffectDuration = -1;

                if (objects != null && objects.length == 2) {
                    if (objects[0] instanceof RoomTile goalTile && objects[1] instanceof RoomTile) {
                        RoomItem topItem = room.getTopItemAt(goalTile.getX(), goalTile.getY(), (objects[0] != objects[1]) ? this : null);

                        if (topItem != null && (topItem.getBaseItem().getEffectM() == this.getBaseItem().getEffectM() || topItem.getBaseItem().getEffectF() == this.getBaseItem().getEffectF())) {
                            return;
                        }

                        if (topItem != null) {
                            nextEffectM = topItem.getBaseItem().getEffectM();
                            nextEffectF = topItem.getBaseItem().getEffectF();
                        } else if (roomUnit.getPreviousEffectId() > 0) {
                            nextEffectF = roomUnit.getPreviousEffectId();
                            nextEffectM = roomUnit.getPreviousEffectId();
                            nextEffectDuration = roomUnit.getPreviousEffectEndTimestamp();
                        }
                    }
                }

                if (roomUnit.getRoomUnitType().equals(RoomUnitType.HABBO)) {
                    Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

                    if (habbo != null) {

                        if (habbo.getHabboInfo().getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0) {
                            habbo.getRoomUnit().giveEffect(nextEffectM, nextEffectDuration);
                            return;
                        }

                        if (habbo.getHabboInfo().getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0) {
                            habbo.getRoomUnit().giveEffect(nextEffectF, nextEffectDuration);
                        }
                    }
                } else if (roomUnit.getRoomUnitType().equals(RoomUnitType.BOT)) {
                    Bot bot = room.getRoomUnitManager().getRoomBotById(roomUnit.getVirtualId());

                    if (bot != null) {
                        if (bot.getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0) {
                            bot.getRoomUnit().giveEffect(nextEffectM, nextEffectDuration);
                            return;
                        }

                        if (bot.getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0) {
                            bot.getRoomUnit().giveEffect(nextEffectF, nextEffectDuration);
                        }
                    }
                }
            }
        }
    }

    public boolean canToggle(Habbo habbo, Room room) {
        if (room.getRoomRightsManager().hasRights(habbo)) return true;

        if (!habbo.getHabboStats().isRentingSpace()) return false;

        RoomItem rentSpace = room.getRoomItemManager().getRoomItemById(habbo.getHabboStats().getRentedItemId());

        return rentSpace != null && RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(this.getX(), this.getY(), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()));

    }

    @Override
    public boolean allowWiredResetState() {
        return true;
    }
}
