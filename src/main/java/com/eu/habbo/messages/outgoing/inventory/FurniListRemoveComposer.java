package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FurniListRemoveComposer extends MessageComposer {
    private final int itemId;

    public FurniListRemoveComposer(final int itemId) {
        this.itemId = itemId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.FurniListRemoveComposer);
        this.response.appendInt(this.itemId);
        return this.response;
    }
}
