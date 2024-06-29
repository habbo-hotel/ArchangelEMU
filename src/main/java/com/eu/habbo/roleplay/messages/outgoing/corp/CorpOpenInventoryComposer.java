package com.eu.habbo.roleplay.messages.outgoing.corp;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corp.Corp;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CorpOpenInventoryComposer extends MessageComposer {
    private final RoomItem roomItem;
    private final Corp corp;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.corpOpenInventoryComposer);
        this.response.appendInt(this.roomItem.getId());
        this.response.appendInt(this.corp.getGuild().getId());
        return this.response;
    }
}
