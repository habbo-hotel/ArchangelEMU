package com.eu.habbo.habbohotel.items;

import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ItemInteraction {
    @Getter
    private final String name;
    @Getter
    private final Class<? extends RoomItem> type;
}
