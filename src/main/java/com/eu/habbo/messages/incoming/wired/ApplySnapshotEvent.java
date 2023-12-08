package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.InteractionWiredMatchFurniSettings;
import com.eu.habbo.habbohotel.rooms.constants.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.constants.RoomTileState;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplySnapshotEvent extends MessageHandler {

    @Override
    public int getRatelimit() {
        return 500;
    }

    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        // Executing Habbo has to be in a Room
        if (!this.client.getHabbo().getRoomUnit().isInRoom()) {
            this.client.sendResponse(new NotificationDialogMessageComposer(
                    BubbleAlertKeys.FURNITURE_PLACEMENT_ERROR.getKey(),
                    FurnitureMovementError.NO_RIGHTS.getErrorCode()
            ));
            return;
        }

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        // Executing Habbo should be able to edit wireds
        if (room == null || (!room.getRoomRightsManager().hasRights(this.client.getHabbo()) && !room.getRoomInfo().isRoomOwner(this.client.getHabbo()))) {
            return;
        }

        List<RoomItem> wireds = new ArrayList<>();
        wireds.addAll(room.getRoomSpecialTypes().getConditions());
        wireds.addAll(room.getRoomSpecialTypes().getEffects());

        // Find the item with the given ID in the room
        Optional<RoomItem> item = wireds.stream()
                .filter(wired -> wired.getId() == itemId)
                .findFirst();

        // If the item exists
        if (item.isEmpty()) {
            return;
        }

        RoomItem wiredItem = item.get();
        // The item should have settings to match furni state, position and rotation
        if (wiredItem instanceof InteractionWiredMatchFurniSettings wired) {

            // Try to apply the set settings to each item
            wired.getMatchSettings().forEach(setting -> {
                RoomItem matchItem = room.getRoomItemManager().getRoomItemById(setting.getItem_id());

                // Match state
                if (wired.shouldMatchState() && matchItem.allowWiredResetState() && !setting.getState().equals(" ") && !matchItem.getExtraData().equals(setting.getState())) {
                    matchItem.setExtraData(setting.getState());
                    room.updateItemState(matchItem);
                }

                RoomTile oldLocation = room.getLayout().getTile(matchItem.getCurrentPosition().getX(), matchItem.getCurrentPosition().getY());
                double oldZ = matchItem.getCurrentZ();

                // Match Position & Rotation
                if (wired.shouldMatchRotation() && !wired.shouldMatchPosition()) {
                    if (matchItem.getRotation() != setting.getRotation() && room.getRoomItemManager().furnitureFitsAt(oldLocation, matchItem, setting.getRotation(), false) == FurnitureMovementError.NONE) {
                        room.getRoomItemManager().moveItemTo(matchItem, oldLocation, setting.getRotation(), null, true, true);
                    }
                } else if (wired.shouldMatchPosition()) {
                    boolean slideAnimation = !wired.shouldMatchRotation() || matchItem.getRotation() == setting.getRotation();
                    RoomTile newLocation = room.getLayout().getTile((short) setting.getX(), (short) setting.getY());
                    int newRotation = wired.shouldMatchRotation() ? setting.getRotation() : matchItem.getRotation();

                    if (newLocation != null && newLocation.getState() != RoomTileState.INVALID && (newLocation != oldLocation || newRotation != matchItem.getRotation()) && room.getRoomItemManager().furnitureFitsAt(newLocation, matchItem, newRotation, true) == FurnitureMovementError.NONE) {
                        boolean sendUpdates = !slideAnimation;
                        if (room.getRoomItemManager().moveItemTo(matchItem, newLocation, newRotation, null, sendUpdates, true) == FurnitureMovementError.NONE && slideAnimation) {
                            room.sendComposer(new FloorItemOnRollerComposer(matchItem, null, oldLocation, oldZ, newLocation, matchItem.getCurrentZ(), 0, room).compose());
                        }
                    }
                }
            });
        }
    }
}
