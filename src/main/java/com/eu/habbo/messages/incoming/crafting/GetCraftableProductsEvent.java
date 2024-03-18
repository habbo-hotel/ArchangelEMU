package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.LimitedEditionSoldOutComposer;
import com.eu.habbo.messages.outgoing.crafting.CraftingRecipeComposer;

public class GetCraftableProductsEvent extends MessageHandler {
    @Override
    public void handle() {
        String recipeName = this.packet.readString();
        CraftingRecipe recipe = Emulator.getGameEnvironment().getCraftingManager().getRecipe(recipeName);

        if (recipe != null) {
            if (!recipe.canBeCrafted()) {
                this.client.sendResponse(new LimitedEditionSoldOutComposer());
                return;
            }

            this.client.sendResponse(new CraftingRecipeComposer(recipe));
        }
    }
}
