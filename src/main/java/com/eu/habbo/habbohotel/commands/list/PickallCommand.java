package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;

public class PickallCommand extends Command {
    public PickallCommand() {
        super("cmd_pick_all");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null) {
            return true;
        }

        if (room.isOwner(gameClient.getHabbo())) {
            room.ejectAll();
            return true;
        }

        room.ejectUserFurni(gameClient.getHabbo().getHabboInfo().getId());

        return true;
    }
}
