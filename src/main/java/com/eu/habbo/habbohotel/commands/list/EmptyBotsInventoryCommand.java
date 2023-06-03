package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.BotInventoryComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import gnu.trove.map.hash.TIntObjectHashMap;

public class EmptyBotsInventoryCommand extends Command {
    public EmptyBotsInventoryCommand() {
        super("cmd_empty_bots");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 1 || (params.length >= 2 && !params[1].equals(getTextsValue("generic.yes")))) {
            if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
                if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().getUserCount() > 10) {
                    gameClient.getHabbo().alert(getTextsValue("commands.succes.cmd_empty_bots.verify").replace("%generic.yes%", getTextsValue("generic.yes")));
                } else {
                    gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_empty_bots.verify").replace("%generic.yes%", getTextsValue("generic.yes")), RoomChatMessageBubbles.ALERT);
                }
            }

            return true;
        }

        if (params.length >= 2 && params[1].equalsIgnoreCase(getTextsValue("generic.yes"))) {

            Habbo habbo = (params.length == 3 && gameClient.getHabbo().hasRight(Permission.ACC_EMPTY_OTHERS)) ? Emulator.getGameEnvironment().getHabboManager().getHabbo(params[2]) : gameClient.getHabbo();

            if (habbo != null) {
                TIntObjectHashMap<Bot> bots = new TIntObjectHashMap<>();
                bots.putAll(habbo.getInventory().getBotsComponent().getBots());
                habbo.getInventory().getBotsComponent().getBots().clear();
                bots.forEachValue(object -> {
                    Emulator.getGameEnvironment().getBotManager().deleteBot(object);
                    return true;
                });

                habbo.getClient().sendResponse(new FurniListInvalidateComposer());
                habbo.getClient().sendResponse(new BotInventoryComposer(habbo));

                gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.succes.cmd_empty_bots.cleared"), habbo.getHabboInfo().getUsername()), RoomChatMessageBubbles.ALERT);
            } else {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_empty_bots"), RoomChatMessageBubbles.ALERT);
            }
        }

        return true;
    }
}