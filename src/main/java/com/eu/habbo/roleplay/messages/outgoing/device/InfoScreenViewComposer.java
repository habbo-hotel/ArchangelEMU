package com.eu.habbo.roleplay.messages.outgoing.device;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InfoScreenViewComposer extends MessageComposer {
    private final RoomItem item;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.infoScreenViewComposer);
        this.response.appendInt(this.item.getId());
        this.response.appendString("CONTENT");
        this.response.appendBoolean(true); // canEdit
        return this.response;
    }
}
