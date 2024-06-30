package com.eu.habbo.roleplay.commands.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;

public class CorpStopWorkCommand extends Command {
    public CorpStopWorkCommand() {
        super("cmd_corp_stopwork");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (!gameClient.getHabbo().getHabboRoleplayStats().isWorking()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_stop_work_no_shift"));
            return true;
        }


        gameClient.getHabbo().getHabboRoleplayStats().setWorking(false);
        gameClient.getHabbo().getRoomUnit().getRoom().sendComposer(new UserRoleplayStatsChangeComposer(gameClient.getHabbo()).compose());
        return true;
    }
}