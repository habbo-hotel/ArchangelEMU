package com.eu.habbo.roleplay.commands.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.actions.WorkShiftAction;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpPosition;

public class CorpStartWorkCommand extends Command {
    public CorpStartWorkCommand() {
        super("cmd_corp_startwork");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().isWorking()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_user_is_already_working"));
            return true;
        }

        Corp userEmployer = Emulator.getGameEnvironment().getCorpManager().getCorpByID(gameClient.getHabbo().getHabboRoleplayStats().getCorp().getGuild().getId());

        if (userEmployer == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_company_does_not_exist"));
            return true;
        }

        CorpPosition userPosition = userEmployer.getPositionByID(gameClient.getHabbo().getHabboRoleplayStats().getCorpPosition().getId());

        if (userPosition == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_position_does_not_exist"));
            return true;
        }

        if (gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId() != userEmployer.getGuild().getRoomId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_start_work_not_in_boundaries"));
            return true;
        }

        Emulator.getThreading().run(new WorkShiftAction(gameClient.getHabbo()));

        return true;
    }
}