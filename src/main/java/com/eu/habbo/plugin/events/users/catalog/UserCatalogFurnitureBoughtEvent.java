package com.eu.habbo.plugin.events.users.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import gnu.trove.set.hash.THashSet;

public class UserCatalogFurnitureBoughtEvent extends UserCatalogEvent {

    public final THashSet<RoomItem> furniture;


    public UserCatalogFurnitureBoughtEvent(Habbo habbo, CatalogItem catalogItem, THashSet<RoomItem> furniture) {
        super(habbo, catalogItem);

        this.furniture = furniture;
    }
}
