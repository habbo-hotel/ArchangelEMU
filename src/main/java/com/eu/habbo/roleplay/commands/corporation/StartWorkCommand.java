package com.eu.habbo.roleplay.commands.corporation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.roleplay.corporations.Corporation;
import com.eu.habbo.roleplay.corporations.CorporationPosition;

public class StartWorkCommand extends Command {
    public StartWorkCommand() {
        super("cmd_startwork");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().getCorporationID() == 0) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_user_is_unemployed"));
            return true;
        }

        if (Emulator.getGameEnvironment().getCorporationManager().getCorporationsShiftManager().isUserWorking(gameClient.getHabbo())) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_user_is_already_working"));
            return true;
        }

        Corporation userEmployer = Emulator.getGameEnvironment().getCorporationManager().getCorporationByID(gameClient.getHabbo().getHabboRoleplayStats().getCorporationID());

        if (userEmployer == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_company_does_not_exist"));
            return true;
        }

        CorporationPosition userPosition = userEmployer.getPositionByID(gameClient.getHabbo().getHabboRoleplayStats().getCorporationPositionID());

        if (userPosition == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_position_does_not_exist"));
            return true;
        }

        Emulator.getGameEnvironment().getCorporationManager().getCorporationsShiftManager().startUserShift(gameClient.getHabbo());

        String jobUniform = gameClient.getHabbo().getHabboInfo().getGender() == HabboGender.M ? userPosition.getMaleFigure() : userPosition.getFemaleFigure();
        gameClient.getHabbo().getHabboInfo().setLook(jobUniform);
        // Trigger user look changed

        String userStartedWorkMessage = Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_success")
                .replace("%companyName%", userEmployer.getName())
                .replace("%positionName%", userPosition.getName());

        gameClient.getHabbo().shout(userStartedWorkMessage);

        return true;
    }
}