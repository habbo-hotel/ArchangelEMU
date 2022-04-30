package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class OneWayDoorStatusMessageComposer extends MessageComposer {
    private final HabboItem item;

    public OneWayDoorStatusMessageComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.oneWayDoorStatusMessageComposer);
        this.response.appendInt(this.item.getId());
        try {
            int state = Integer.valueOf(this.item.getExtradata());
            this.response.appendInt(state);
        } catch (Exception e) {
            this.response.appendInt(0);
        }

        return this.response;
    }
}
