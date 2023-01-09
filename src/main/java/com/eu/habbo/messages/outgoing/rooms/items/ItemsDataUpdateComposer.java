package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class ItemsDataUpdateComposer extends MessageComposer {
    private final Set<HabboItem> items;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.objectsDataUpdateMessageComposer);
        this.response.appendInt(this.items.size());

        for (HabboItem item : this.items) {
            this.response.appendInt(item.getId());
            item.serializeExtradata(this.response);
        }

        return this.response;
    }
}