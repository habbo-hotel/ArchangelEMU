package com.eu.habbo.habbohotel.catalog.marketplace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MarketPlaceState {

    OPEN(1),

    SOLD(2),

    CLOSED(3);

    private final int state;

    public static MarketPlaceState getType(int type) {
        return switch (type) {
            case 1 -> OPEN;
            case 2 -> SOLD;
            case 3 -> CLOSED;
            default -> CLOSED;
        };

    }
}
