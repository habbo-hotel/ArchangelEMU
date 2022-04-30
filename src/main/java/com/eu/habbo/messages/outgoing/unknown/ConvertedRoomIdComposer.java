package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ConvertedRoomIdComposer extends MessageComposer {
    private final String unknownString1;
    private final int unknownInt1;

    public ConvertedRoomIdComposer(String unknownString1, int unknownInt1) {
        this.unknownString1 = unknownString1;
        this.unknownInt1 = unknownInt1;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.convertedRoomIdComposer);
        this.response.appendString(this.unknownString1);
        this.response.appendInt(this.unknownInt1);
        return this.response;
    }
}