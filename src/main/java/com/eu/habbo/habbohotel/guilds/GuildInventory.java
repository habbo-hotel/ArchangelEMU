package com.eu.habbo.habbohotel.guilds;

import com.eu.habbo.habbohotel.guilds.inventory.ItemsComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class GuildInventory {
    public static int MAXIMUM_ITEMS = 10000;
    private final Guild guild;
    private ItemsComponent itemsComponent;

    public GuildInventory(Guild guild) {
        this.guild = guild;
        try {
            this.itemsComponent = new ItemsComponent(this, this.guild);
        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }



    public void dispose() {
        this.itemsComponent.dispose();
        this.itemsComponent = null;
    }
}
