package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ItemUpdateMessageComposer extends MessageComposer {
    private final HabboItem item;

    public ItemUpdateMessageComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.itemUpdateMessageComposer);
        this.item.serializeWallData(this.response);
        this.response.appendString(this.item.getUserId() + "");
        return this.response;
    }
}
