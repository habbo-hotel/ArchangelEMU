package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemAddMessageComposer extends MessageComposer {
    private final RoomItem item;
    private final String itemOwnerName;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.itemAddMessageComposer);
        this.item.serializeWallData(this.response);
        this.response.appendString(this.itemOwnerName);
        return this.response;
    }
}
