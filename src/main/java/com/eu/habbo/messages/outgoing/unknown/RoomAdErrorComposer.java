package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomAdErrorComposer extends MessageComposer {
    private final int errorCode;
    private final String unknownString;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomAdErrorComposer);
        this.response.appendInt(this.errorCode);
        this.response.appendString(this.unknownString);
        return this.response;
    }
}