package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
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
        this.trigger.serializeWiredData(this.response, this.room);
        this.trigger.needsUpdate(true);
        return this.response;
    }
}
