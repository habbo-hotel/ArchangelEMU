package com.eu.habbo.roleplay.commands.corporation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.government.GovernmentManager;

public class QuitJobCommand extends Command {
    public QuitJobCommand() {
        super("cmd_quitjob");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().getCorporationID() == GovernmentManager.getInstance().getWelfareCorp().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_quitjob_unemployed"));
            return true;
        }

        gameClient.getHabbo().getHabboRoleplayStats().setCorporationID(GovernmentManager.getInstance().getWelfareCorp().getId());
        gameClient.getHabbo().getHabboRoleplayStats().setCorporationPositionID(GovernmentManager.getInstance().getWelfareCorp().getPositionByOrderID(0).getId());
        gameClient.getHabbo().getHabboRoleplayStats().run();

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_quitjob_success"));

        return true;
    }
}