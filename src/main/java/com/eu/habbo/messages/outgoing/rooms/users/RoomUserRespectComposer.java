package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomUserRespectComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomUserRespectComposer);
        this.response.appendInt(this.habbo.getHabboInfo().getId());
        this.response.appendInt(this.habbo.getHabboStats().getRespectPointsReceived());
        return this.response;
    }
}
