package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class SitCommand extends Command {
    public SitCommand() {
        super("cmd_sit");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getRiding() == null) //TODO Make this an event plugin which fires that can be cancelled
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().makeSit(gameClient.getHabbo());
        return true;
    }
}
