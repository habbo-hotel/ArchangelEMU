package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ExpressionMessageComposer extends MessageComposer {
    private RoomUserAction action;
    private RoomUnit roomUnit;

    public ExpressionMessageComposer(RoomUnit roomUnit, RoomUserAction action) {
        this.roomUnit = roomUnit;
        this.action = action;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.ExpressionMessageComposer);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.action.getAction());
        return this.response;
    }
}
