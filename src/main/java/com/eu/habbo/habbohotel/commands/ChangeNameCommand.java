package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.users.UserObjectComposer;

public class ChangeNameCommand extends Command {
    public ChangeNameCommand() {
        super("cmd_changename", Emulator.getTexts().getValue("commands.keys.cmd_changename").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        gameClient.getHabbo().getHabboStats().allowNameChange = !gameClient.getHabbo().getHabboStats().allowNameChange;
        gameClient.sendResponse(new UserObjectComposer(gameClient.getHabbo()));
        return true;
    }
}
