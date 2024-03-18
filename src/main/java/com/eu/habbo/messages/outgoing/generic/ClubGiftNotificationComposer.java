package com.eu.habbo.messages.outgoing.generic;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClubGiftNotificationComposer extends MessageComposer {
    private final int count;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.clubGiftNotificationComposer);
        this.response.appendInt(this.count);
        return this.response;
    }
}
