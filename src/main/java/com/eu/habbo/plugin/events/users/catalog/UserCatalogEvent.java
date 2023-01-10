package com.eu.habbo.plugin.events.users.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;
import lombok.Getter;

@Getter
public class UserCatalogEvent extends UserEvent {

    private final CatalogItem catalogItem;


    public UserCatalogEvent(Habbo habbo, CatalogItem catalogItem) {
        super(habbo);

        this.catalogItem = catalogItem;
    }
}