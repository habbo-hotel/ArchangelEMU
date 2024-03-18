package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LatencyPingResponseMessageComposer extends MessageComposer {
    private final int id;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.latencyPingResponseMessageComposer);
        this.response.appendInt(this.id);
        return this.response;
    }
}
