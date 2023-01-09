package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomMessageNotificationMessageComposer extends MessageComposer {
    private final Room room;
    private final int count;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomMessageNotificationMessageComposer);
        this.response.appendInt(this.room.getId());
        this.response.appendString(this.room.getName());
        this.response.appendInt(this.count);
        return this.response;
    }
}