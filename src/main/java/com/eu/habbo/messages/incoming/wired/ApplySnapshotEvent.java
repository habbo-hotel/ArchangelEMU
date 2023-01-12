package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.InteractionWiredMatchFurniSettings;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.users.HabboItem;
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

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        // Executing Habbo should be able to edit wireds
        if (room == null || (!room.hasRights(this.client.getHabbo()) && !room.isOwner(this.client.getHabbo()))) {
            return;
        }

        List<HabboItem> wireds = new ArrayList<>();
        wireds.addAll(room.getRoomSpecialTypes().getConditions());
        wireds.addAll(room.getRoomSpecialTypes().getEffects());

        // Find the item with the given ID in the room
        Optional<HabboItem> item = wireds.stream()
                .filter(wired -> wired.getId() == itemId)
                .findFirst();

        // If the item exists
        if (item.isEmpty()) {
            return;
        }

        HabboItem wiredItem = item.get();
        // The item should have settings to match furni state, position and rotation
        if (wiredItem instanceof InteractionWiredMatchFurniSettings wired) {

            // Try to apply the set settings to each item
            wired.getMatchFurniSettings().forEach(setting -> {
                HabboItem matchItem = room.getHabboItem(setting.getItem_id());

                // Match state
                if (wired.shouldMatchState() && matchItem.allowWiredResetState() && !setting.getState().equals(" ") && !matchItem.getExtradata().equals(setting.getState())) {
                    matchItem.setExtradata(setting.getState());
                    room.updateItemState(matchItem);
                }

                RoomTile oldLocation = room.getLayout().getTile(matchItem.getX(), matchItem.getY());
                double oldZ = matchItem.getZ();

                // Match Position & Rotation
                if (wired.shouldMatchRotation() && !wired.shouldMatchPosition()) {
                    if (matchItem.getRotation() != setting.getRotation() && room.furnitureFitsAt(oldLocation, matchItem, setting.getRotation(), false) == FurnitureMovementError.NONE) {
                        room.moveFurniTo(matchItem, oldLocation, setting.getRotation(), null, true);
                    }
                } else if (wired.shouldMatchPosition()) {
                    boolean slideAnimation = !wired.shouldMatchRotation() || matchItem.getRotation() == setting.getRotation();
                    RoomTile newLocation = room.getLayout().getTile((short) setting.getX(), (short) setting.getY());
                    int newRotation = wired.shouldMatchRotation() ? setting.getRotation() : matchItem.getRotation();

                    if (newLocation != null && newLocation.getState() != RoomTileState.INVALID && (newLocation != oldLocation || newRotation != matchItem.getRotation())
                            && room.furnitureFitsAt(newLocation, matchItem, newRotation, true) == FurnitureMovementError.NONE
                            && room.moveFurniTo(matchItem, newLocation, newRotation, null, !slideAnimation) == FurnitureMovementError.NONE && slideAnimation) {
                        room.sendComposer(new FloorItemOnRollerComposer(matchItem, null, oldLocation, oldZ, newLocation, matchItem.getZ(), 0, room).compose());
                    }
                }
            });
        }
    }
}
