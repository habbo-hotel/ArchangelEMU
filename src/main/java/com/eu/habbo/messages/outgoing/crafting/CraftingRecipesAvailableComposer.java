package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CraftingRecipesAvailableComposer extends MessageComposer {
    private final int count;
    private final boolean found;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.craftingRecipesAvailableComposer);
        this.response.appendInt((this.found ? -1 : 0) + this.count);
        this.response.appendBoolean(this.found);
        return this.response;
    }
}
