package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RequestSpamWallPostItMessageComposer extends MessageComposer {
    private final HabboItem item;

    public RequestSpamWallPostItMessageComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.RequestSpamWallPostItMessageComposer);
        this.response.appendInt(this.item == null ? -1234 : this.item.getId());
        this.response.appendString("");
        return this.response;
    }
}
