package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.catalog.*;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceConfigurationComposer;

public class UpdateCatalogCommand extends Command {
    public UpdateCatalogCommand() {
        super("cmd_update_catalogue");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getCatalogManager().initialize();
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new CatalogPublishedMessageComposer());
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new BuildersClubFurniCountMessageComposer(0));
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new BundleDiscountRulesetMessageComposer());
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new MarketplaceConfigurationComposer());
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new GiftWrappingConfigurationComposer());
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new RecyclerPrizesComposer());
        Emulator.getGameEnvironment().getCraftingManager().reload();
        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_catalog"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}
