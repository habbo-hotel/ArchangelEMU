package com.eu.habbo.habbohotel.campaign;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.users.subscriptions.SubscriptionHabboClub;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Getter
public class CalendarRewardObject {
    private final int id;
    private final String productName;
    private final String customImage;
    private final int credits;
    private final int pixels;
    private final int points;
    private final int pointsType;
    private final String badge;
    private final int itemId;
    private final String subscriptionType;
    private final int subscriptionDays;

    public CalendarRewardObject(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.productName = set.getString("product_name");
        this.customImage = set.getString("custom_image");
        this.credits = set.getInt("credits");
        this.pixels = set.getInt("pixels");
        this.points = set.getInt("points");
        this.pointsType = set.getInt("points_type");
        this.badge = set.getString("badge");
        this.itemId = set.getInt("item_id");
        this.subscriptionType = set.getString("subscription_type");
        this.subscriptionDays = set.getInt("subscription_days");
    }

    public void give(Habbo habbo) {
        if (this.credits > 0) {
            habbo.giveCredits(this.credits);
        }

        if (this.pixels > 0) {
            habbo.givePixels((int) (this.pixels * (habbo.getHabboStats().hasActiveClub() ? CalendarManager.HC_MODIFIER : 1.0)));
        }

        if (this.points > 0) {
            habbo.givePoints(this.pointsType, this.points);
        }

        if (!this.badge.isEmpty()) {
            habbo.addBadge(this.badge);
        }

        if (this.subscriptionType != null && !this.subscriptionType.isEmpty()) {
            if ("HABBO_CLUB".equals(this.subscriptionType)) {
                habbo.getHabboStats().createSubscription(SubscriptionHabboClub.HABBO_CLUB, this.subscriptionDays * 86400);
            } else {
                habbo.getHabboStats().createSubscription(this.subscriptionType, this.subscriptionDays * 86400);
            }
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
                habbo.getClient().sendResponse(new UnseenItemsComposer(habboItem));
                habbo.getClient().sendResponse(new FurniListInvalidateComposer());
            }
        }
    }


    public Item getItem() {
        return Emulator.getGameEnvironment().getItemManager().getItem(this.itemId);
    }

}
