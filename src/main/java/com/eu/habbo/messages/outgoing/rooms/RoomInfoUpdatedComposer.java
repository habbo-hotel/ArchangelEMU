package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomInfoUpdatedComposer extends MessageComposer {
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomInfoUpdatedComposer);
        this.response.appendInt(this.room.getId());
        return this.response;
    }
}
