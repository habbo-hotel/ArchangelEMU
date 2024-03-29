package com.eu.habbo.roleplay.commands.corporation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corps.Corporation;
import com.eu.habbo.roleplay.corps.CorporationPosition;
import com.eu.habbo.roleplay.corps.CorporationManager;

public class CorpSuperHireCommand extends Command {
    public CorpSuperHireCommand() {
        super("cmd_corp_superhire");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return true;
        }

        String targetedUsername = params[1];

        if (targetedUsername == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.user_not_found"));
            return true;
        }

        Habbo targetedHabbo = gameClient.getHabbo().getRoomUnit().getRoom().getRoomUnitManager().getRoomHabboByUsername(targetedUsername);

        if (targetedHabbo == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.user_not_found").replace("%username%", targetedUsername));
            return true;
        }

        Integer corporationId = params[2] != null ? Integer.parseInt(params[2]) : null;

        if (corporationId == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_superhire_invalid_corp"));
            return true;
        }

        Corporation matchingCorp = CorporationManager.getInstance().getCorporationByID(corporationId);

        if (matchingCorp == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_superhire_invalid_corp"));
            return true;
        }


        Integer positionId = params[3] != null ? Integer.parseInt(params[3]) : null;

        if (positionId == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_superhire_invalid_position"));
            return true;
        }

        CorporationPosition matchingPosition = matchingCorp.getPositionByID(positionId);

        if (matchingPosition == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_superhire_invalid_position"));
            return true;
        }

        targetedHabbo.getHabboRoleplayStats().setCorporation(corporationId, positionId);

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_superhire_success")
                .replace("%username%", targetedHabbo.getHabboInfo().getUsername())
                .replace("%corp%", matchingCorp.getName())
                .replace("%position%", matchingPosition.getName()));

        targetedHabbo.shout(Emulator.getTexts().getValue("generic.roleplay.started_new_job").
                replace("%corp%", matchingCorp.getName())
                .replace("%position%", matchingPosition.getName()));

        return true;
    }
}