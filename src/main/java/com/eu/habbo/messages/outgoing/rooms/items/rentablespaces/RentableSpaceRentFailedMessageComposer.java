package com.eu.habbo.messages.outgoing.rooms.items.rentablespaces;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RentableSpaceRentFailedMessageComposer extends MessageComposer {
    private final int itemId;

    public RentableSpaceRentFailedMessageComposer(int itemId) {
        this.itemId = itemId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.rentableSpaceRentFailedMessageComposer);
        this.response.appendInt(this.itemId);
        return this.response;
    }
}
