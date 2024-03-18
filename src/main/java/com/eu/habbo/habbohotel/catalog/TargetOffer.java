package com.eu.habbo.habbohotel.catalog;


import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.cache.HabboOfferPurchase;
import com.eu.habbo.messages.ServerMessage;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TargetOffer {
    public static int ACTIVE_TARGET_OFFER_ID = 0;

    @Getter
    private final int id;
    @Getter
    private final int catalogItem;
    @Getter
    private final String identifier;
    @Getter
    private final int priceInCredits;
    @Getter
    private final int priceInActivityPoints;
    @Getter
    private final int activityPointsType;
    @Getter
    private final int purchaseLimit;
    @Getter
    private final int expirationTime;
    @Getter
    private final String title;
    @Getter
    private final String description;
    @Getter
    private final String imageUrl;
    @Getter
    private final String icon;
    @Getter
    private final String[] vars;

    public TargetOffer(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.identifier = set.getString("offer_code");
        this.priceInCredits = set.getInt("credits");
        this.priceInActivityPoints = set.getInt("points");
        this.activityPointsType = set.getInt("points_type");
        this.title = set.getString("title");
        this.description = set.getString("description");
        this.imageUrl = set.getString("image");
        this.icon = set.getString("icon");
        this.purchaseLimit = set.getInt("purchase_limit");
        this.expirationTime = set.getInt("end_timestamp");
        this.vars = set.getString("vars").split(";");
        this.catalogItem = set.getInt("catalog_item");
    }

    public void serialize(ServerMessage message, HabboOfferPurchase purchase) {
        message.appendInt(purchase.getState());
        message.appendInt(this.id);
        message.appendString(this.identifier);
        message.appendString(this.identifier);
        message.appendInt(this.priceInCredits);
        message.appendInt(this.priceInActivityPoints);
        message.appendInt(this.activityPointsType);
        message.appendInt(Math.max(this.purchaseLimit - purchase.getAmount(), 0));
        message.appendInt(Math.max(this.expirationTime - Emulator.getIntUnixTimestamp(), 0));
        message.appendString(this.title);
        message.appendString(this.description);
        message.appendString(this.imageUrl);
        message.appendString(this.icon);
        message.appendInt(0);
        message.appendInt(this.vars.length);
        for (String variable : this.vars) {
            message.appendString(variable);
        }
    }

}