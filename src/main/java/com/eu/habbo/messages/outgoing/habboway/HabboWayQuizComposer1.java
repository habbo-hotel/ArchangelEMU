package com.eu.habbo.messages.outgoing.habboway;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HabboWayQuizComposer1 extends MessageComposer {
    public final String name;
    public final int[] items;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboWayQuizComposer1);
        this.response.appendString(this.name);
        this.response.appendInt(this.items.length);
        for (int item : this.items) {
            this.response.appendInt(item);
        }
        return this.response;
    }
}