package com.eu.habbo.roleplay.commands.corporation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.corps.CorporationShift;

public class CorpStopWorkCommand extends Command {
    public CorpStopWorkCommand() {
        super("cmd_corp_stopwork");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        CorporationShift userShift = Emulator.getGameEnvironment().getCorporationManager().getCorporationsShiftManager().getUserShift(gameClient.getHabbo());


        if (userShift == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_stop_work_no_shift"));
            return true;
        }

        Emulator.getGameEnvironment().getCorporationManager().getCorporationsShiftManager().stopUserShift(gameClient.getHabbo());
        gameClient.getHabbo().getHabboInfo().setLook(userShift.getOldLook());
        // Trigger user look changed
        return true;
    }
}