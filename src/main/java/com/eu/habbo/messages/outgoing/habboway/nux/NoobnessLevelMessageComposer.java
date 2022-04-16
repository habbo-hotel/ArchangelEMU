package com.eu.habbo.messages.outgoing.habboway.nux;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NoobnessLevelMessageComposer extends MessageComposer {
    private final Habbo habbo;

    public NoobnessLevelMessageComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.NoobnessLevelMessageComposer);
        this.response.appendInt(this.habbo.noobStatus());
        return this.response;
    }
}
