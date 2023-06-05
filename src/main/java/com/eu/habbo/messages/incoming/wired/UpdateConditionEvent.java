package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.WiredValidationErrorComposer;
import com.eu.habbo.messages.outgoing.wired.WiredSavedComposer;

public class UpdateConditionEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null) {
            if (room.hasRights(this.client.getHabbo()) || room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasRight(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasRight(Permission.ACC_MOVEROTATE)) {
                InteractionWiredCondition condition = room.getRoomSpecialTypes().getCondition(itemId);

                if (condition != null) {
                    WiredSettings settings = InteractionWired.readSettings(this.packet, false);

                    if (condition.saveData(settings)) {
                        this.client.sendResponse(new WiredSavedComposer());

                                condition.needsUpdate(true);

                        Emulator.getThreading().run(condition);
                    } else {
                        this.client.sendResponse(new WiredValidationErrorComposer("There was an error while saving that condition"));
                    }

                }
            }
        }
    }
}
