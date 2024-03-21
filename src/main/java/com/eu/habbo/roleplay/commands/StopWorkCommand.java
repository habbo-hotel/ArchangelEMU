package com.eu.habbo.roleplay.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.roleplay.corporations.CorporationShift;

public class StopWorkCommand extends Command {
    public StopWorkCommand() {
        super("cmd_stopwork");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        CorporationShift userShift = Emulator.getGameEnvironment().getCorporationsManager().getCorporationsShiftManager().getUserShift(gameClient.getHabbo());


        if (userShift == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.roleplay.cmd_stop_work_no_shift"));
            return true;
        }

        Emulator.getGameEnvironment().getCorporationsManager().getCorporationsShiftManager().stopUserShift(gameClient.getHabbo());
        gameClient.getHabbo().getHabboInfo().setLook(userShift.getOldLook());
        // Trigger user look changed
        return true;
    }
}