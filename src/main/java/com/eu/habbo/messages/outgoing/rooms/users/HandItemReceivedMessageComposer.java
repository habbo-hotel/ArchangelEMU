package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HandItemReceivedMessageComposer extends MessageComposer {
    private RoomUnit from;
    private int handItem;

    public HandItemReceivedMessageComposer(RoomUnit from, int handItem) {
        this.from = from;
        this.handItem = handItem;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.handItemReceivedMessageComposer);
        this.response.appendInt(this.from.getId());
        this.response.appendInt(this.handItem);
        return this.response;
    }
}
