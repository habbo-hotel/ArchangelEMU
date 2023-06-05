package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.WiredValidationErrorComposer;
import com.eu.habbo.messages.outgoing.wired.WiredSavedComposer;

public class UpdateActionEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null) {
            if (room.hasRights(this.client.getHabbo()) || room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasRight(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasRight(Permission.ACC_MOVEROTATE)) {
                InteractionWiredEffect effect = room.getRoomSpecialTypes().getEffect(itemId);

                try {
                    if (effect == null)
                        throw new WiredSaveException(String.format("Wired effect with item id %s not found in room", itemId));

                    WiredSettings settings = InteractionWired.readSettings(this.packet, true);
                    if (effect.saveData(settings, this.client)) {
                        this.client.sendResponse(new WiredSavedComposer());
                        effect.needsUpdate(true);
                        Emulator.getThreading().run(effect);
                    }


                }
                catch (WiredSaveException e) {
                    this.client.sendResponse(new WiredValidationErrorComposer(e.getMessage()));
                }
            }
        }
    }
}
