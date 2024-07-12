package com.eu.habbo.roleplay.commands.police;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.police.PoliceReport;
import com.eu.habbo.roleplay.police.PoliceReportManager;

public class ResolveReportCommand extends Command {
    public ResolveReportCommand() {
        super("cmd_police_resolve_report");
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

        Corp corp = gameClient.getHabbo().getHabboRoleplayStats().getCorp();

        if (corp == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.unemployed"));
            return true;
        }

        if (!corp.getTags().contains(CorpTag.POLICE)) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.police_only"));
            return true;
        }

        if (!gameClient.getHabbo().getHabboRoleplayStats().isWorking()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.must_be_working"));
            return true;
        }

        PoliceReport policeReport = new PoliceReport(gameClient.getHabbo(), gameClient.getHabbo().getRoomUnit().getRoom(), message, null, false);
        PoliceReportManager.getInstance().addPoliceReport(policeReport);

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("roleplay.police.cfh_resolved"));

        return true;
    }
}