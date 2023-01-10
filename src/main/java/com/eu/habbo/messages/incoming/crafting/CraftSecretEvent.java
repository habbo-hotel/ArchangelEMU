package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.crafting.CraftingAltar;
import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.LimitedEditionSoldOutComposer;
import com.eu.habbo.messages.outgoing.crafting.CraftingResultComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListRemoveComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;
import java.util.Set;

public class CraftSecretEvent extends MessageHandler {
    @Override
    public void handle() {
        int altarId = this.packet.readInt();
        int count = this.packet.readInt();

        HabboItem craftingAltar = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(altarId);

        if (craftingAltar != null) {
            CraftingAltar altar = Emulator.getGameEnvironment().getCraftingManager().getAltar(craftingAltar.getBaseItem());

            if (altar != null) {
                Set<HabboItem> habboItems = new THashSet<>();
                Map<Item, Integer> items = new THashMap<>();

                for (int i = 0; i < count; i++) {
                    HabboItem habboItem = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(this.packet.readInt());

                    if (habboItem == null) {
                        this.client.sendResponse(new CraftingResultComposer(null));
                        return;
                    }

                    habboItems.add(habboItem);

                    if (!items.containsKey(habboItem.getBaseItem())) {
                        items.put(habboItem.getBaseItem(), 0);
                    }

                    items.put(habboItem.getBaseItem(), items.get(habboItem.getBaseItem()) + 1);
                }

                CraftingRecipe recipe = altar.getRecipe(items);

                if (recipe != null) {
                    if (!recipe.canBeCrafted()) {
                        this.client.sendResponse(new LimitedEditionSoldOutComposer());
                        return;
                    }

                    HabboItem rewardItem = Emulator.getGameEnvironment().getItemManager().createItem(this.client.getHabbo().getHabboInfo().getId(), recipe.getReward(), 0, 0, "");

                    if (rewardItem != null) {
                        if (recipe.isLimited()) {
                            recipe.decrease();
                        }

                        if (!recipe.getAchievement().isEmpty()) {
                            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement(recipe.getAchievement()));
                        }

                        this.client.sendResponse(new CraftingResultComposer(recipe));
                        if (!this.client.getHabbo().getHabboStats().hasRecipe(recipe.getId())) {
                            this.client.getHabbo().getHabboStats().addRecipe(recipe.getId());
                            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("AtcgSecret"));
                        }
                        this.client.getHabbo().getInventory().getItemsComponent().addItem(rewardItem);
                        this.client.sendResponse(new UnseenItemsComposer(rewardItem));
                        for (HabboItem item : habboItems) {
                            this.client.getHabbo().getInventory().getItemsComponent().removeHabboItem(item);
                            this.client.sendResponse(new FurniListRemoveComposer(item.getGiftAdjustedId()));
                            Emulator.getThreading().run(new QueryDeleteHabboItem(item.getId()));
                        }
                        this.client.sendResponse(new FurniListInvalidateComposer());

                        return;
                    }
                }
            }
        }

        this.client.sendResponse(new CraftingResultComposer(null));
    }
}
