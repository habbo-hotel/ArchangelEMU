package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.LatencyPingResponseMessageComposer;

public class LatencyPingRequestEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new LatencyPingResponseMessageComposer(this.packet.readInt()));
    }
}
