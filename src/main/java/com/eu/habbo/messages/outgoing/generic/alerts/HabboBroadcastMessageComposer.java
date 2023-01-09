package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HabboBroadcastMessageComposer extends MessageComposer {
    private final String message;

    public HabboBroadcastMessageComposer(String message, Habbo habbo) {
        this.message = message.replace("%username%", habbo.getHabboInfo().getUsername());
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboBroadcastMessageComposer);

        this.response.appendString(this.message);

        return this.response;
    }
}
