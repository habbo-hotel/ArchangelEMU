package com.eu.habbo.roleplay.commands.gang;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.roleplay.gangs.Gang;
import com.eu.habbo.roleplay.gangs.GangPosition;
import com.eu.habbo.roleplay.gangs.GangManager;

public class GangCreateCommand extends Command {
    public GangCreateCommand() {
        super("cmd_gang_create");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().getGangID() != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_create_already_in_gang"));
            return true;
        }

        String gangName = params[1];

        if (gangName == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_create_name_is_required"));
            return true;
        }

        Gang matchingGangByName = GangManager.getInstance().getGangByName(gangName);

        if (matchingGangByName != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_create_name_is_taken"));
            return true;
        }

        Room currentRoom = gameClient.getHabbo().getRoomUnit().getRoom();

        if (currentRoom == null || currentRoom.getRoomInfo().getOwnerInfo().getId() != gameClient.getHabbo().getHabboInfo().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_create_room_not_available"));
            return true;
        }

        Gang createdGang = GangManager.getInstance().createGangWithDefaultPosition(gangName, gameClient.getHabbo().getHabboInfo().getId(), currentRoom.getRoomInfo().getId());

        GangPosition createdPosition = createdGang.getPositionByOrderID(1);

        gameClient.getHabbo().getHabboRoleplayStats().setGangID(createdGang.getId());
        gameClient.getHabbo().getHabboRoleplayStats().setGangPositionID(createdPosition.getId());

        gameClient.getHabbo().getHabboRoleplayStats().run();

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_create_success").replace("%gang%", createdGang.getName()));
        return true;
    }
}