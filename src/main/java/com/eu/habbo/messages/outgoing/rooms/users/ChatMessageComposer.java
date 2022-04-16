package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ChatMessageComposer extends MessageComposer {
    private final RoomChatMessage roomChatMessage;

    public ChatMessageComposer(RoomChatMessage roomChatMessage) {
        this.roomChatMessage = roomChatMessage;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.ChatMessageComposer);

        if (this.roomChatMessage.getMessage().isEmpty())
            return null;

        this.roomChatMessage.serialize(this.response);
        return this.response;
    }
}
