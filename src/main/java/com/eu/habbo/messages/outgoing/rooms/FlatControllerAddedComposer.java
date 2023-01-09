package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlatControllerAddedComposer extends MessageComposer {
    private final Room room;
    private final int userId;
    private final String userName;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.flatControllerAddedComposer);
        this.response.appendInt(this.room.getId());
        this.response.appendInt(this.userId);
        this.response.appendString(this.userName);
        return this.response;
    }
}
