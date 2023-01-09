package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AvailabilityStatusMessageComposer extends MessageComposer {
    private final boolean isOpen;
    private final boolean isShuttingDown;
    private final boolean isAuthenticHabbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.availabilityStatusMessageComposer);

        this.response.appendBoolean(isOpen);//isOpen
        this.response.appendBoolean(isShuttingDown);//onShutdown
        this.response.appendBoolean(isAuthenticHabbo);//isAuthenticHabbo
        return this.response;
    }
}
