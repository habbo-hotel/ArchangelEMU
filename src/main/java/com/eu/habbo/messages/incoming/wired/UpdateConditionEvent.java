package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.WiredValidationErrorComposer;
import com.eu.habbo.messages.outgoing.wired.WiredSavedComposer;

public class UpdateConditionEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room != null) {
            if (room.getRoomRightsManager().hasRights(this.client.getHabbo()) || room.getRoomInfo().getOwnerInfo().getId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermissionRight(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermissionRight(Permission.ACC_MOVEROTATE)) {
                InteractionWiredCondition condition = room.getRoomSpecialTypes().getCondition(itemId);

                try {
                    if (condition == null) {
                        throw new WiredSaveException(String.format("Wired condition with item id %s not found in room", itemId));
                    }

                    condition.saveWiredSettings(this.packet, room);
                    this.client.sendResponse(new WiredSavedComposer());
                    condition.setSqlUpdateNeeded(true);
                    Emulator.getThreading().run(condition);
                }
                catch (WiredSaveException e) {
                    this.client.sendResponse(new WiredValidationErrorComposer(e.getMessage()));
                }
            }
        }
    }
}
