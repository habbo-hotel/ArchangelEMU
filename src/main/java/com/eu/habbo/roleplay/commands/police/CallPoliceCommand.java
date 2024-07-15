package com.eu.habbo.roleplay.commands.police;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.messages.outgoing.police.PoliceCallInfoComposer;
import com.eu.habbo.roleplay.police.PoliceReport;
import com.eu.habbo.roleplay.police.PoliceReportManager;
import com.eu.habbo.roleplay.users.HabboRoleplayHelper;

import java.util.List;

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

        List<Habbo> policeOnline = HabboRoleplayHelper.getUsersByCorpTag(CorpTag.POLICE);
        List<Habbo> policeWorking = HabboRoleplayHelper.getUsersWorking(policeOnline);

        for (Habbo policeOfficer : policeWorking) {
            policeOfficer.getClient().sendResponse(new PoliceCallInfoComposer(policeReport));
        }

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("roleplay.police.cfh_success"));

        return true;
    }
}