package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.outgoing.catalog.*;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceConfigurationComposer;
import com.google.gson.Gson;

public class UpdateCatalog extends RCONMessage<UpdateCatalog.JSONUpdateCatalog> {

    public UpdateCatalog() {
        super(JSONUpdateCatalog.class);
    }

    @Override
    public void handle(Gson gson, JSONUpdateCatalog json) {
        Emulator.getGameEnvironment().getCatalogManager().initialize();
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new CatalogPublishedMessageComposer());
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new BuildersClubFurniCountMessageComposer(0));
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new BundleDiscountRulesetMessageComposer());
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new MarketplaceConfigurationComposer());
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new GiftWrappingConfigurationComposer());
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new RecyclerPrizesComposer());
        Emulator.getGameEnvironment().getCraftingManager().reload();
    }

    static class JSONUpdateCatalog {
    }
}