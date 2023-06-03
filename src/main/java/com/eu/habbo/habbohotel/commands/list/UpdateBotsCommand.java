package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class UpdateBotsCommand extends Command {
    public UpdateBotsCommand() {
        super("cmd_update_bots");
    }
    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        return Emulator.getGameEnvironment().getBotManager().reload();
    }
}
