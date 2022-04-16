package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ModeratorMessageComposer extends MessageComposer {
    private final String message;
    private final String link;

    public ModeratorMessageComposer(String message, String link) {
        this.message = message;
        this.link = link;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.ModeratorMessageComposer);
        this.response.appendString(this.message);
        this.response.appendString(this.link);
        return this.response;
    }
}
