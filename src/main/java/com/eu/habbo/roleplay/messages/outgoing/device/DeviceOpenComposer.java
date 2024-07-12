package com.eu.habbo.roleplay.messages.outgoing.device;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeviceOpenComposer extends MessageComposer {
    private final RoomItem item;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.phoneOpenComposer);
        this.response.appendInt(this.item.getId());
        this.response.appendString(item.getBaseItem().getInteractionType().getName());
        return this.response;
    }
}
