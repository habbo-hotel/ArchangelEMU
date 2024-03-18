package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiceValueMessageComposer extends MessageComposer {
    private final int id;
    private final int value;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.diceValueMessageComposer);
        this.response.appendInt(this.id);
        this.response.appendInt(this.value);
        return this.response;
    }
}