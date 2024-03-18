package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class CraftingRecipeComposer extends MessageComposer {
    private final CraftingRecipe recipe;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.craftingRecipeComposer);
        this.response.appendInt(this.recipe.getIngredients().size());

        for (Map.Entry<Item, Integer> ingredient : this.recipe.getIngredients().entrySet()) {
            this.response.appendInt(ingredient.getValue());
            this.response.appendString(ingredient.getKey().getName());
        }
        return this.response;
    }
}
