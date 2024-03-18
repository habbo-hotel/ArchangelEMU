package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvatarEffectAddedMessageComposer extends MessageComposer {
    private final EffectsComponent.HabboEffect effect;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.avatarEffectAddedMessageComposer);
        this.response.appendInt(this.effect.getEffect()); //Type
        this.response.appendInt(0); //Unknown Costume?
        this.response.appendInt(effect.getDuration() > 0 ? effect.getDuration() : Integer.MAX_VALUE); //Duration
        this.response.appendBoolean(effect.getDuration() <= 0); //Is active
        return this.response;
    }
}