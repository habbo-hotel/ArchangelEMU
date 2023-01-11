package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvatarEffectActivatedMessageComposer extends MessageComposer {
    private final EffectsComponent.HabboEffect effect;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.avatarEffectActivatedMessageComposer);
        this.response.appendInt(this.effect.getEffect()); //Type
        this.response.appendInt(this.effect.getDuration()); //Duration
        this.response.appendBoolean(this.effect.isEnabled()); //activated
        return this.response;
    }
}