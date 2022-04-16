package com.eu.habbo.messages.outgoing.events.mysticbox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GotMysteryBoxPrizeMessageComposer extends MessageComposer {
    private final String type;
    private final int itemId;

    public GotMysteryBoxPrizeMessageComposer(String type, int itemId) {
        this.type = type;
        this.itemId = itemId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.GotMysteryBoxPrizeMessageComposer);
        this.response.appendString(this.type);
        this.response.appendInt(this.itemId);
        return this.response;
    }
}
