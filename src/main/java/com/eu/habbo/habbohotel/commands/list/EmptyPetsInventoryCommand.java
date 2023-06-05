package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.inventory.PetInventoryComposer;
import gnu.trove.map.hash.TIntObjectHashMap;

public class EmptyPetsInventoryCommand extends Command {
    public EmptyPetsInventoryCommand() {
        super("cmd_empty_pets");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 1 || (params.length >= 2 && !params[1].equals(getTextsValue("generic.yes")))) {
            if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
                if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().getUserCount() > 10) {
                    gameClient.getHabbo().alert(getTextsValue("commands.succes.cmd_empty_pets.verify").replace("%generic.yes%", getTextsValue("generic.yes")));
                } else {
                    gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_empty_pets.verify").replace("%generic.yes%", getTextsValue("generic.yes")), RoomChatMessageBubbles.ALERT);
                }
            }

            return true;
        }

        if (params.length >= 2 && params[1].equalsIgnoreCase(getTextsValue("generic.yes"))) {

            Habbo habbo = (params.length == 3 && gameClient.getHabbo().hasRight(Permission.ACC_EMPTY_OTHERS)) ? Emulator.getGameEnvironment().getHabboManager().getHabbo(params[2]) : gameClient.getHabbo();

            if (habbo != null) {
                TIntObjectHashMap<Pet> pets = new TIntObjectHashMap<>(habbo.getInventory().getPetsComponent().getPets());
                habbo.getInventory().getPetsComponent().getPets().clear();
                pets.forEachValue(object -> {
                    Emulator.getGameEnvironment().getPetManager().deletePet(object);
                    return true;
                });

                habbo.getClient().sendResponse(new FurniListInvalidateComposer());
                habbo.getClient().sendResponse(new PetInventoryComposer(habbo));

                gameClient.getHabbo().whisper(replaceUsername(getTextsValue("commands.succes.cmd_empty_pets.cleared"), habbo.getHabboInfo().getUsername()), RoomChatMessageBubbles.ALERT);
            } else {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_empty_pets"), RoomChatMessageBubbles.ALERT);
            }
        }

        return true;
    }
}