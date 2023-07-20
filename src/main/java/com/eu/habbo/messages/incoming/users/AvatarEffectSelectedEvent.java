package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;

public class AvatarEffectSelectedEvent extends MessageHandler {
    @Override
    public void handle() {
        int effectId = this.packet.readInt();

        if (effectId > 0) {
            if (this.client.getHabbo().getInventory().getEffectsComponent().ownsEffect(effectId)) {
                this.client.getHabbo().getInventory().getEffectsComponent().enableEffect(effectId);
            }
        } else {
            this.client.getHabbo().getInventory().getEffectsComponent().setActivatedEffect(0);

            if (this.client.getHabbo().getRoomUnit().getRoom() != null) {
                this.client.getHabbo().getRoomUnit().giveEffect(0, -1);
            }
        }
    }
}