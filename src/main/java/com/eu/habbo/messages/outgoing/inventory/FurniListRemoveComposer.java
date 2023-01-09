package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FurniListRemoveComposer extends MessageComposer {
    private final int itemId;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.furniListRemoveComposer);
        this.response.appendInt(this.itemId);
        return this.response;
    }
}
