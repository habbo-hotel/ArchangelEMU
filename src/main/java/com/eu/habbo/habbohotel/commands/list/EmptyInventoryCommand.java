package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.FurniListComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItems;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class EmptyInventoryCommand extends Command {
    public EmptyInventoryCommand() {
        super("cmd_empty");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 1 || (params.length == 2 && !params[1].equals(getTextsValue("generic.yes")))) {
            if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
                if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().getUserCount() > 10) {
                    gameClient.getHabbo().alert(getTextsValue("commands.succes.cmd_empty.verify").replace("%generic.yes%", getTextsValue("generic.yes")));
                } else {
                    gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_empty.verify").replace("%generic.yes%", getTextsValue("generic.yes")), RoomChatMessageBubbles.ALERT);
                }
            }

            return true;
        }

        if (params.length >= 2 && params[1].equalsIgnoreCase(getTextsValue("generic.yes"))) {

            Habbo habbo = (params.length == 3 && gameClient.getHabbo().hasRight(Permission.ACC_EMPTY_OTHERS)) ? Emulator.getGameEnvironment().getHabboManager().getHabbo(params[2]) : gameClient.getHabbo();

            if (habbo != null) {
                TIntObjectMap<HabboItem> items = new TIntObjectHashMap<>();
                items.putAll(habbo.getInventory().getItemsComponent().getItems());
                habbo.getInventory().getItemsComponent().getItems().clear();
                Emulator.getThreading().run(new QueryDeleteHabboItems(items));

                habbo.getClient().sendResponse(new FurniListInvalidateComposer());
                habbo.getClient().sendResponse(new FurniListComposer(0, 1, gameClient.getHabbo().getInventory().getItemsComponent().getItems()));
                

                gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.succes.cmd_empty.cleared"), habbo.getHabboInfo().getUsername()), RoomChatMessageBubbles.ALERT);
            } else {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_empty"), RoomChatMessageBubbles.ALERT);
            }
        }

        return true;
    }
}