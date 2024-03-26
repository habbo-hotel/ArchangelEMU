package com.eu.habbo.roleplay.commands.corporation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.government.GovernmentManager;

public class FireCommand extends Command {
    public FireCommand() {
        super("cmd_fire");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
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

        if (targetedHabbo == gameClient.getHabbo()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_fire_user_is_self"));
            return true;
        }

        if (!gameClient.getHabbo().getHabboRoleplayStats().getCorporationPosition().isCanFire()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_fire_not_allowed"));
            return true;
        }

        if (gameClient.getHabbo().getHabboRoleplayStats().getCorporationPosition().getOrderID() <= targetedHabbo.getHabboRoleplayStats().getCorporationPosition().getOrderID()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_fire_not_allowed"));
            return true;
        }

        gameClient.getHabbo().getHabboRoleplayStats().setCorporationID(GovernmentManager.getInstance().getWelfareCorp().getId());
        gameClient.getHabbo().getHabboRoleplayStats().setCorporationPositionID(GovernmentManager.getInstance().getWelfareCorp().getPositionByOrderID(1).getId());

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_fire_success").replace("%username%", targetedHabbo.getHabboInfo().getUsername()));
        targetedHabbo.shout(Emulator.getTexts().getValue("commands.roleplay.cmd_fire_impacted"));

        return true;
    }
}