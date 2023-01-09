package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExpressionMessageComposer extends MessageComposer {
    private final RoomUnit roomUnit;
    private final RoomUserAction action;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.expressionMessageComposer);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.action.getAction());
        return this.response;
    }
}
