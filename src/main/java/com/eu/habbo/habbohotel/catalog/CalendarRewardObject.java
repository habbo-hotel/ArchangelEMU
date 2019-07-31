package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CalendarRewardObject {
    private final int id;
    private final String customImage;
    private final int credits;
    private final int points;
    private final int pointsType;
    private final String badge;
    private final int itemId;

    public CalendarRewardObject(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.customImage = set.getString("custom_image");
        this.credits = set.getInt("credits");
        this.points = set.getInt("points");
        this.pointsType = set.getInt("points_type");
        this.badge = set.getString("badge");
        this.itemId = set.getInt("item_id");
    }

    public void give(Habbo habbo) {
        if (this.credits > 0) {
            habbo.giveCredits(this.credits);
        }

        if (this.points > 0) {
            habbo.givePoints(this.pointsType, this.points);
        }

        if (!this.badge.isEmpty()) {
            habbo.addBadge(this.badge);
        }

        if (this.itemId > 0) {
            Item item = getItem();

            if (item != null) {
                HabboItem habboItem = Emulator.getGameEnvironment().getItemManager().createItem(
                        habbo.getHabboInfo().getId(),
                        item,
                        0,
                        0,
                        "");
                habbo.getInventory().getItemsComponent().addItem(habboItem);
                habbo.getClient().sendResponse(new AddHabboItemComposer(habboItem));
                habbo.getClient().sendResponse(new InventoryRefreshComposer());
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public String getCustomImage() {
        return this.customImage;
    }

    public int getCredits() {
        return this.credits;
    }

    public int getPoints() {
        return this.points;
    }

    public int getPointsType() {
        return this.pointsType;
    }

    public String getBadge() {
        return this.badge;
    }

    public Item getItem() {
        return Emulator.getGameEnvironment().getItemManager().getItem(this.itemId);
    }
}
