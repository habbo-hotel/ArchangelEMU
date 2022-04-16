package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.LatencyPingResponseMessageComposer;

public class PingEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new LatencyPingResponseMessageComposer(this.packet.readInt()));
    }
}
