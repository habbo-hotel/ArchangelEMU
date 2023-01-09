package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlatControllerRemovedComposer extends MessageComposer {
    private final Room room;
    private final int userId;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.flatControllerRemovedComposer);
        this.response.appendInt(this.room.getId());
        this.response.appendInt(this.userId);
        return this.response;
    }
}
