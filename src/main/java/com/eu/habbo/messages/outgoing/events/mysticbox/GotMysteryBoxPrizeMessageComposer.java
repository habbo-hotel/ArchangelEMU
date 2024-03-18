package com.eu.habbo.messages.outgoing.events.mysticbox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GotMysteryBoxPrizeMessageComposer extends MessageComposer {
    private final String type;
    private final int itemId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.gotMysteryBoxPrizeMessageComposer);
        this.response.appendString(this.type);
        this.response.appendInt(this.itemId);
        return this.response;
    }
}
