package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class TestCommand extends Command {
    public TestCommand() {
        super("cmd_test");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {

        return true;
    }
}
