package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.crafting.CraftingAltar;
import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.crafting.CraftingRecipesAvailableComposer;
import gnu.trove.map.hash.THashMap;

import java.util.Map;

public class GetCraftingRecipesAvailableEvent extends MessageHandler {
    @Override
    public void handle() {
        int altarId = this.packet.readInt();

        RoomItem item = this.client.getHabbo().getRoomUnit().getRoom().getRoomItemManager().getRoomItemById(altarId);

        CraftingAltar altar = Emulator.getGameEnvironment().getCraftingManager().getAltar(item.getBaseItem());

        if (altar != null) {
            Map<Item, Integer> items = new THashMap<>();

            int count = this.packet.readInt();
            for (int i = 0; i < count; i++) {
                RoomItem roomItem = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(this.packet.readInt());

                if (roomItem != null) {
                    if (!items.containsKey(roomItem.getBaseItem())) {
                        items.put(roomItem.getBaseItem(), 0);
                    }

                    items.put(roomItem.getBaseItem(), items.get(roomItem.getBaseItem()) + 1);
                }
            }

            CraftingRecipe equalsRecipe = altar.getRecipe(items);
            if (equalsRecipe != null && this.client.getHabbo().getHabboStats().hasRecipe(equalsRecipe.getId())) {
                //this.client.sendResponse(new CraftingRecipesAvailableComposer(-1, true));
                //this.client.sendResponse(new CraftingRecipeComposer(equalsRecipe));
                //this.client.sendResponse(new CraftingResultComposer(equalsRecipe, true));
                return;
            }
            Map<CraftingRecipe, Boolean> recipes = altar.matchRecipes(items);

            boolean found = false;
            int c = recipes.size();
            for (Map.Entry<CraftingRecipe, Boolean> set : recipes.entrySet()) {
                if (this.client.getHabbo().getHabboStats().hasRecipe(set.getKey().getId())) {
                    c--;
                    continue;
                }

                if (set.getValue()) {
                    found = true;
                    break;
                }
            }
            this.client.sendResponse(new CraftingRecipesAvailableComposer(c, found));
        }
    }
}
