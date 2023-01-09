package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
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
        this.condition.serializeWiredData(this.response, this.room);
        this.condition.needsUpdate(true);
        return this.response;
    }
}
