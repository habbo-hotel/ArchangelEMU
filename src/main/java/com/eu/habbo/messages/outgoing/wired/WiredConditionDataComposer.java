package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WiredConditionDataComposer extends MessageComposer {
    private final InteractionWiredCondition condition;
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.wiredConditionDataComposer);

        this.response.appendBoolean(false);
        this.response.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        this.response.appendInt(this.condition.getWiredSettings().getItemIds().size());

        for (int itemId : this.condition.getWiredSettings().getItemIds()) {
            this.response.appendInt(itemId);
        }

        this.response.appendInt(this.condition.getBaseItem().getSpriteId());
        this.response.appendInt(this.condition.getId());
        this.response.appendString(this.condition.getWiredSettings().getStringParam());
        this.response.appendInt(this.condition.getWiredSettings().getIntegerParams().size());

        for (int param : this.condition.getWiredSettings().getIntegerParams()) {
            this.response.appendInt(param);
        }

        this.response.appendInt(this.condition.getWiredSettings().getSelectionType());
        this.response.appendInt(this.condition.getType().getCode());

        this.condition.needsUpdate(true);
        return this.response;
    }
}
