package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemRemoveMessageComposer extends MessageComposer {
    private final RoomItem item;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.itemRemoveMessageComposer);
        this.response.appendString(this.item.getId() + "");
        this.response.appendInt(this.item.getOwnerId());
        return this.response;
    }
}
