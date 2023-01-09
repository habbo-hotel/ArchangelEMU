package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BotForceOpenContextMenuComposer extends MessageComposer {
    private final int botId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.botForceOpenContextMenuComposer);
        this.response.appendInt(this.botId);
        return this.response;
    }
}