package com.eu.habbo.roleplay.messages.outgoing.device;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PhoneOpenComposer extends MessageComposer {
    private final int itemID;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.phoneOpenComposer);
        this.response.appendInt(this.itemID);
        return this.response;
    }
}
