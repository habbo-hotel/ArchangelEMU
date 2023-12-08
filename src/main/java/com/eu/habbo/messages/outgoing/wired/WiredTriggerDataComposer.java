package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WiredTriggerDataComposer extends MessageComposer {
    private final InteractionWiredTrigger trigger;
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.wiredTriggerDataComposer);

        this.response.appendBoolean(false);
        this.response.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        this.response.appendInt(this.trigger.getWiredSettings().getItemIds().size());

        for (int itemId : this.trigger.getWiredSettings().getItemIds()) {
            this.response.appendInt(itemId);
        }

        this.response.appendInt(this.trigger.getBaseItem().getSpriteId());
        this.response.appendInt(this.trigger.getId());
        this.response.appendString(this.trigger.getWiredSettings().getStringParam());
        this.response.appendInt(this.trigger.getWiredSettings().getIntegerParams().size());

        for (int param : this.trigger.getWiredSettings().getIntegerParams()) {
            this.response.appendInt(param);
        }

        this.response.appendInt(this.trigger.getWiredSettings().getSelectionType());
        this.response.appendInt(this.trigger.getType().getCode());
        this.response.appendInt(this.trigger.getBlockedEffects(this.room).size());

        for(int blockedTrigger : this.trigger.getBlockedEffects(this.room)) {
            this.response.appendInt(blockedTrigger);
        }

        this.trigger.setSqlUpdateNeeded(true);
        return this.response;
    }
}
