package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OneWayDoorStatusMessageComposer extends MessageComposer {
    private final HabboItem item;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.oneWayDoorStatusMessageComposer);
        this.response.appendInt(this.item.getId());
        try {
            int state = Integer.parseInt(this.item.getExtradata());
            this.response.appendInt(state);
        } catch (Exception e) {
            this.response.appendInt(0);
        }

        return this.response;
    }
}
