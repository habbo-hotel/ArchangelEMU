package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KnockKnockUnknownComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(478); //TODO Hardcoded header
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendInt(this.habbo.getHabboInfo().getId());
        return this.response;
    }
}
