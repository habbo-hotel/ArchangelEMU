package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SnowWarsLoadingArenaComposer extends MessageComposer {
    private final int count;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3850);
        this.response.appendInt(this.count); //GameID?
        this.response.appendInt(0); //Count
        //this.response.appendInt(1); //ItemID to dispose?
        return this.response;
    }
}
