package com.eu.habbo.habbohotel.items;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedeemableSubscriptionType {
    HABBO_CLUB("hc"),
    BUILDERS_CLUB("bc");
    public final String subscriptionType;

    public static RedeemableSubscriptionType fromString(String subscriptionType) {
        if (subscriptionType == null) return null;

        return switch (subscriptionType) {
            case "hc" -> HABBO_CLUB;
            case "bc" -> BUILDERS_CLUB;
            default -> null;
        };

    }
}
