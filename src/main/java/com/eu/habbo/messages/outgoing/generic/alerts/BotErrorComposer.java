package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BotErrorComposer extends MessageComposer {
    public static final int ROOM_ERROR_BOTS_FORBIDDEN_IN_HOTEL = 0;
    public static final int ROOM_ERROR_BOTS_FORBIDDEN_IN_FLAT = 1;
    public static final int ROOM_ERROR_MAX_BOTS = 2;
    public static final int ROOM_ERROR_BOTS_SELECTED_TILE_NOT_FREE = 3;
    public static final int ROOM_ERROR_BOTS_NAME_NOT_ACCEPT = 4;

    private final int errorCode;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.botErrorComposer);
        this.response.appendInt(this.errorCode);
        return this.response;
    }
}
