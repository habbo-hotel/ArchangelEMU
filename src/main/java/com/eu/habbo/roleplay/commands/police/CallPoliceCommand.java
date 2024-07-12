package com.eu.habbo.roleplay.commands.police;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.police.PoliceReport;
import com.eu.habbo.roleplay.police.PoliceReportManager;

public class CallPoliceCommand extends Command {
    public CallPoliceCommand() {
        super("cmd_police_call_for_help");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null || params.length == 0) {
            return true;
        }

        String message = params[1];

        if (message == null) {
            return true;
        }

        if (gameClient.getHabbo().getHabboRoleplayStats().isDead()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.not_allowed"));
            return true;
        }

        if (gameClient.getHabbo().getHabboRoleplayStats().isJailed()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.not_allowed"));
            return true;
        }

        PoliceReport policeReport = new PoliceReport(gameClient.getHabbo(), gameClient.getHabbo().getRoomUnit().getRoom(), message, null, false);
        PoliceReportManager.getInstance().addPoliceReport(policeReport);

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("roleplay.police.cfh_success"));

        return true;
    }
}