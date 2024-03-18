package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;

public class AvatarEffectActivatedEvent extends MessageHandler {
    @Override
    public void handle() {
        int effectId = this.packet.readInt();

        if (this.client.getHabbo().getInventory().getEffectsComponent().ownsEffect(effectId)) {
            this.client.getHabbo().getInventory().getEffectsComponent().activateEffect(effectId);
        }
    }
}