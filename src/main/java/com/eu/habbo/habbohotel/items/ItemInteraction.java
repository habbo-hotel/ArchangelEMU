package com.eu.habbo.habbohotel.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ItemInteraction {
    @Getter
    private final String name;
    @Getter
    private final Class<? extends HabboItem> type;
}
