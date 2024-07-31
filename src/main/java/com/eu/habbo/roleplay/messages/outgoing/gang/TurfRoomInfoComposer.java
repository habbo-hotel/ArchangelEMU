package com.eu.habbo.roleplay.messages.outgoing.gang;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TurfRoomInfoComposer extends MessageComposer {
    private final Room room;;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.turfRoomInfoComposer);
        this.response.appendInt(this.room.getRoomInfo().getId());
        this.response.appendInt(this.room.getRoomInfo() != null ? this.room.getRoomInfo().getGuild().getId() : -1);
        return this.response;
    }
}
