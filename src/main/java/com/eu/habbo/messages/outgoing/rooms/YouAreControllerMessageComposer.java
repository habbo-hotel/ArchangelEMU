package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class YouAreControllerMessageComposer extends MessageComposer {
    private final RoomRightLevels type;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.youAreControllerMessageComposer);
        this.response.appendInt(this.type.getLevel());
        return this.response;
    }
}
