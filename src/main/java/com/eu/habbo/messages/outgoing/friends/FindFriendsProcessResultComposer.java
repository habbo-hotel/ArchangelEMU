package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindFriendsProcessResultComposer extends MessageComposer {
    public static final int NO_ROOM_FOUND = 0;
    public static final int ROOM_FOUND = 1;

    private final int errorCode;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.findFriendsProcessResultComposer);
        this.response.appendInt(this.errorCode);
        return this.response;
    }
}
