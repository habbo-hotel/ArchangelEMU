package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;

public class HideWiredCommand extends Command {
    public HideWiredCommand() {
        super("cmd_hidewired");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
        if (room == null) {
            return true;
        }

        if (room.isOwner(gameClient.getHabbo())) {
            room.setHideWired(!room.isHideWired());
            gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_hidewired." + (room.isHideWired() ? "hidden" : "shown")));
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.errors.cmd_hidewired.permission"));
        }

        return true;
    }
}
