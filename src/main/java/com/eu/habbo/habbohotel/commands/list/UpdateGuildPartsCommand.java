package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class UpdateGuildPartsCommand extends Command {
    public UpdateGuildPartsCommand() {
        super("cmd_update_guildparts");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getGuildManager().loadGuildParts();
        Emulator.getBadgeImager().reload();
        return true;
    }
}
