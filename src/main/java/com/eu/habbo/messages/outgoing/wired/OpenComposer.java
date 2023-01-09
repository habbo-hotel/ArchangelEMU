package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OpenComposer extends MessageComposer {
    private final HabboItem item;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.openComposer);
        this.response.appendInt(this.item.getId());
        return this.response;
    }
}