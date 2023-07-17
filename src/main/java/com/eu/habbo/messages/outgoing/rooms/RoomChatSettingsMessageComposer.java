package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomChatSettingsMessageComposer extends MessageComposer {
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomChatSettingsMessageComposer);
        this.response.appendInt(this.room.getRoomInfo().getChatMode());
        this.response.appendInt(this.room.getRoomInfo().getChatWeight());
        this.response.appendInt(this.room.getRoomInfo().getChatSpeed());
        this.response.appendInt(this.room.getRoomInfo().getChatDistance());
        this.response.appendInt(this.room.getRoomInfo().getChatProtection());
        return this.response;
    }
}
