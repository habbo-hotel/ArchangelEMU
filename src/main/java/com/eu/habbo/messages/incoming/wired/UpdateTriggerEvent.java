package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.WiredValidationErrorComposer;
import com.eu.habbo.messages.outgoing.wired.WiredSavedComposer;

public class UpdateTriggerEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null) {
            if (room.hasRights(this.client.getHabbo()) || room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasRight(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasRight(Permission.ACC_MOVEROTATE)) {
                InteractionWiredTrigger trigger = room.getRoomSpecialTypes().getTrigger(itemId);

                if (trigger != null) {
                    WiredSettings settings = InteractionWired.readSettings(this.packet, false);

                    if (trigger.saveData(settings)) {
                        this.client.sendResponse(new WiredSavedComposer());

                                trigger.needsUpdate(true);

                        Emulator.getThreading().run(trigger);
                    } else {
                        this.client.sendResponse(new WiredValidationErrorComposer("There was an error while saving that trigger"));
                    }
                }
            }
        }
    }
}
