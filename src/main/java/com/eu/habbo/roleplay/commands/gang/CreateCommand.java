package com.eu.habbo.roleplay.commands.gang;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.gangs.Gang;
import com.eu.habbo.roleplay.gangs.GangsManager;

public class CreateCommand extends Command {
    public CreateCommand() {
        super("cmd_gang_create");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().getGangPositionID() != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_create_already_in_gang"));
            return true;
        }

        String gangName = params[2];

        if (gangName == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_create_name_is_required"));
            return true;
        }

        Gang matchingGangByName = GangsManager.getInstance().getGangByName(gangName);

        if (matchingGangByName != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_gang_name_is_taken"));
            return true;
        }

        return true;
    }
}