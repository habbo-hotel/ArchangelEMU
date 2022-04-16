package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BotRemovedFromInventoryComposer extends MessageComposer {
    private final Bot bot;

    public BotRemovedFromInventoryComposer(Bot bot) {
        this.bot = bot;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.BotRemovedFromInventoryComposer);
        this.response.appendInt(this.bot.getId());
        return this.response;
    }
}
