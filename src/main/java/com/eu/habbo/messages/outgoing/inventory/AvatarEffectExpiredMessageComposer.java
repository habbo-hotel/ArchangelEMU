package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AvatarEffectExpiredMessageComposer extends MessageComposer {
    public final EffectsComponent.HabboEffect effect;

    public AvatarEffectExpiredMessageComposer(EffectsComponent.HabboEffect effect) {
        this.effect = effect;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.avatarEffectExpiredMessageComposer);
        this.response.appendInt(this.effect.effect);
        return this.response;
    }
}