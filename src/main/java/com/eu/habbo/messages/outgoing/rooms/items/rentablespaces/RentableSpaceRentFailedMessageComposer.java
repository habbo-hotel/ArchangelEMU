package com.eu.habbo.messages.outgoing.rooms.items.rentablespaces;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RentableSpaceRentFailedMessageComposer extends MessageComposer {
    private final int itemId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.rentableSpaceRentFailedMessageComposer);
        this.response.appendInt(this.itemId);
        return this.response;
    }
}
