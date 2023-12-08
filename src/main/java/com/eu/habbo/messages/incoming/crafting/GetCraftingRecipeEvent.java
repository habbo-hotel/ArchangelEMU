package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.crafting.CraftingAltar;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.crafting.CraftableProductsComposer;

public class GetCraftingRecipeEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();
        RoomItem item = this.client.getHabbo().getRoomUnit().getRoom().getRoomItemManager().getRoomItemById(itemId);

        if (item != null) {
            CraftingAltar altar = Emulator.getGameEnvironment().getCraftingManager().getAltar(item.getBaseItem());

            if (altar != null) {
                this.client.sendResponse(new CraftableProductsComposer(altar.getRecipesForHabbo(this.client.getHabbo()), altar.getIngredients()));
            }
        }
    }
}
