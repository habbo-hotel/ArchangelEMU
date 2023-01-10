package com.eu.habbo.habbohotel.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CraftingRecipe {
    @Getter
    private final int id;
    @Getter
    private final String name;
    @Getter
    private final Item reward;
    @Getter
    private final boolean secret;
    @Getter
    private final String achievement;
    @Getter
    private final boolean limited;
    @Getter
    private final THashMap<Item, Integer> ingredients;
    @Getter
    private int remaining;

    public CraftingRecipe(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("product_name");
        this.reward = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("reward"));
        this.secret = set.getString("secret").equals("1");
        this.achievement = set.getString("achievement");
        this.limited = set.getString("limited").equals("1");
        this.remaining = set.getInt("remaining");

        this.ingredients = new THashMap<>();
    }

    public boolean canBeCrafted() {
        return !this.limited || this.remaining > 0;
    }

    public synchronized boolean decrease() {
        if (this.remaining > 0) {
            this.remaining--;
            return true;
        }

        return false;
    }

    public void addIngredient(Item item, int amount) {
        this.ingredients.put(item, amount);
    }

    public int getAmountNeeded(Item item) {
        return this.ingredients.get(item);
    }

    public boolean hasIngredient(Item item) {
        return this.ingredients.containsKey(item);
    }

}