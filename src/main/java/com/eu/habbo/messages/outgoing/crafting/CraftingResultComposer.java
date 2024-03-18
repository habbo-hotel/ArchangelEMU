package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CraftingResultComposer extends MessageComposer {
    private final CraftingRecipe recipe;
    private final boolean success;

    public CraftingResultComposer(CraftingRecipe recipe) {
        this.recipe = recipe;
        this.success = this.recipe != null;
    }


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.craftingResultComposer);

        this.response.appendBoolean(this.success); //succes

        if (this.recipe != null) {
            this.response.appendString(this.recipe.getName());
            this.response.appendString(this.recipe.getReward().getName());
        }

        return this.response;
    }
}
