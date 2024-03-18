package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EnableNotificationsComposer extends MessageComposer {
    private final boolean enabled;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.infoFeedEnableMessageComposer);
        this.response.appendBoolean(this.enabled);
        return this.response;
    }
}
