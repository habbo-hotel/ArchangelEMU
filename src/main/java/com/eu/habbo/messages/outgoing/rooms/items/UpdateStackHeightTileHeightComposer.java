package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateStackHeightTileHeightComposer extends MessageComposer {
    private final HabboItem item;
    private final int height;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.updateStackHeightTileHeightComposer);
        this.response.appendInt(this.item.getId());
        this.response.appendInt(this.height);
        return this.response;
    }
}
