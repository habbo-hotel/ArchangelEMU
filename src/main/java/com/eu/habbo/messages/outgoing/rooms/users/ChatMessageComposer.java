package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatMessageComposer extends MessageComposer {
    private final RoomChatMessage roomChatMessage;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.chatMessageComposer);

        if (this.roomChatMessage.getMessage().isEmpty())
            return null;

        this.roomChatMessage.serialize(this.response);
        return this.response;
    }
}
