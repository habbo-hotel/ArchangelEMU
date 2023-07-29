package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemUpdateMessageComposer extends MessageComposer {
    private final RoomItem item;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.itemUpdateMessageComposer);
        this.item.serializeWallData(this.response);
        this.response.appendString(String.valueOf(this.item.getOwnerInfo().getId()));
        return this.response;
    }
}
