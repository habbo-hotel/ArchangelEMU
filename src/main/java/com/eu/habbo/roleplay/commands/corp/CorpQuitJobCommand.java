package com.eu.habbo.roleplay.commands.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.government.GovernmentManager;

public class CorpQuitJobCommand extends Command {
    public CorpQuitJobCommand() {
        super("cmd_corp_quitjob");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboRoleplayStats().getCorp().getGuild().getId() == GovernmentManager.getInstance().getWelfareCorp().getGuild().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_quitjob_unemployed"));
            return true;
        }

        Corp welfareCorp = GovernmentManager.getInstance().getWelfareCorp();

        gameClient.getHabbo().getHabboRoleplayStats().setCorp(welfareCorp.getGuild().getId(), welfareCorp.getPositionByOrderID(1).getId());

        gameClient.getHabbo().shout(Emulator.getTexts().getValue("commands.roleplay.cmd_quitjob_success"));

        return true;
    }
}