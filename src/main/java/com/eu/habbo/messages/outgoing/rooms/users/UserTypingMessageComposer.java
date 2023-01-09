package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserTypingMessageComposer extends MessageComposer {
    private final RoomUnit roomUnit;
    private final boolean typing;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userTypingMessageComposer);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.typing ? 1 : 0);
        return this.response;
    }
}
