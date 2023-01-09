package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRemoveMessageComposer extends MessageComposer {
    private final RoomUnit roomUnit;

     @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userRemoveMessageComposer);
        this.response.appendString(this.roomUnit.getId() + "");
        return this.response;
    }
}
