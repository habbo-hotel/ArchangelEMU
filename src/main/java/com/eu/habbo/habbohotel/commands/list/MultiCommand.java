package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.rooms.items.RequestSpamWallPostItMessageComposer;

public class MultiCommand extends Command {
    public MultiCommand() {
        super("cmd_multi");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        gameClient.sendResponse(new RequestSpamWallPostItMessageComposer(null));
        return true;
    }
}
