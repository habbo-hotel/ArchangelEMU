package com.eu.habbo.plugin.events.users.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.HashSet;

public class UserCatalogFurnitureBoughtEvent extends UserCatalogEvent {

    public final HashSet<RoomItem> furniture;


    public UserCatalogFurnitureBoughtEvent(Habbo habbo, CatalogItem catalogItem, HashSet<RoomItem> furniture) {
        super(habbo, catalogItem);

        this.furniture = furniture;
    }
}
