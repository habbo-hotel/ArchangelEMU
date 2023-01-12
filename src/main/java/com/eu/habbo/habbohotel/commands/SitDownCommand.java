package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;

public class SitDownCommand extends Command {
    public SitDownCommand() {
        super("cmd_sitdown", Emulator.getTexts().getValue("commands.keys.cmd_sitdown").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos().forEach(habbo -> {
            if (habbo.getRoomUnit().isWalking()) {
                habbo.getRoomUnit().stopWalking();
            } else if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.SIT)) {
                return;
            }
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().makeSit(habbo);
        });

        return true;
    }
}
