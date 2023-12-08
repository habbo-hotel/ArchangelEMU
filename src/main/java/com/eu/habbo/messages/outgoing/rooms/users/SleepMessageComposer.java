package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SleepMessageComposer extends MessageComposer {
    private final RoomHabbo roomHabbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.sleepMessageComposer);
        this.response.appendInt(this.roomHabbo.getVirtualId());
        this.response.appendBoolean(this.roomHabbo.isIdle());
        return this.response;
    }
}
