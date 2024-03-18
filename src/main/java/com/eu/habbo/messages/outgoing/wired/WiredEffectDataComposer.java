package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WiredEffectDataComposer extends MessageComposer {
    private final InteractionWiredEffect effect;
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.wiredEffectDataComposer);

        this.response.appendBoolean(false);
        this.response.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        this.response.appendInt(this.effect.getWiredSettings().getItemIds().size());

        for (int itemId : this.effect.getWiredSettings().getItemIds()) {
            this.response.appendInt(itemId);
        }

        this.response.appendInt(this.effect.getBaseItem().getSpriteId());
        this.response.appendInt(this.effect.getId());
        this.response.appendString(this.effect.getWiredSettings().getStringParam());
        this.response.appendInt(this.effect.getWiredSettings().getIntegerParams().size());

        for (int param : this.effect.getWiredSettings().getIntegerParams()) {
            this.response.appendInt(param);
        }

        this.response.appendInt(this.effect.getWiredSettings().getSelectionType());
        this.response.appendInt(this.effect.getType().getCode());
        this.response.appendInt(this.effect.getWiredSettings().getDelay());

        this.response.appendInt(this.effect.getBlockedTriggers(this.room).size());

        for(int blockedTrigger : this.effect.getBlockedTriggers(this.room)) {
            this.response.appendInt(blockedTrigger);
        }

        this.effect.setSqlUpdateNeeded(true);

        return this.response;
    }
}
