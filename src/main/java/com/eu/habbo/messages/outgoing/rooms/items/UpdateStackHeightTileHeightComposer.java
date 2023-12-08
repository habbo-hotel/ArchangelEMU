package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateStackHeightTileHeightComposer extends MessageComposer {
    private final RoomItem item;
    private final int height;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.updateStackHeightTileHeightComposer);
        this.response.appendInt(this.item.getId());
        this.response.appendInt(this.height);
        return this.response;
    }
}
