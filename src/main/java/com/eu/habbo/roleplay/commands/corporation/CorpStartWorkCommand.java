package com.eu.habbo.roleplay.commands.corporation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.roleplay.corporations.Corporation;
import com.eu.habbo.roleplay.corporations.CorporationPosition;

public class CorpStartWorkCommand extends Command {
    public CorpStartWorkCommand() {
        super("cmd_corp_startwork");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (Emulator.getGameEnvironment().getCorporationManager().getCorporationsShiftManager().isUserWorking(gameClient.getHabbo())) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_user_is_already_working"));
            return true;
        }

        Corporation userEmployer = Emulator.getGameEnvironment().getCorporationManager().getCorporationByID(gameClient.getHabbo().getHabboRoleplayStats().getCorporation().getId());

        if (userEmployer == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_company_does_not_exist"));
            return true;
        }

        CorporationPosition userPosition = userEmployer.getPositionByID(gameClient.getHabbo().getHabboRoleplayStats().getCorporationPosition().getId());

        if (userPosition == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_position_does_not_exist"));
            return true;
        }

        Emulator.getGameEnvironment().getCorporationManager().getCorporationsShiftManager().startUserShift(gameClient.getHabbo());

        String jobUniform = gameClient.getHabbo().getHabboInfo().getGender() == HabboGender.M ? userPosition.getMaleFigure() : userPosition.getFemaleFigure();
        gameClient.getHabbo().getHabboInfo().setLook(jobUniform);
        // Trigger user look changed

        String userStartedWorkMessage = Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_success")
                .replace("%corp%", userEmployer.getName())
                .replace("%position%", userPosition.getName());

        gameClient.getHabbo().shout(userStartedWorkMessage);

        return true;
    }
}