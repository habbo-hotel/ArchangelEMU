package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HandItemReceivedMessageComposer extends MessageComposer {
    private final RoomUnit from;
    private final int handItem;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.handItemReceivedMessageComposer);
        this.response.appendInt(this.from.getId());
        this.response.appendInt(this.handItem);
        return this.response;
    }
}
