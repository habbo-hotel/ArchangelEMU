package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ModeratorMessageComposer extends MessageComposer {
    private final String message;
    private final String link;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.moderatorMessageComposer);
        this.response.appendString(this.message);
        this.response.appendString(this.link);
        return this.response;
    }
}
