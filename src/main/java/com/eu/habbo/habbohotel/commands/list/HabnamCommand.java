package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class HabnamCommand extends Command {
    public HabnamCommand() {
        super("cmd_habnam");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboStats().hasActiveClub() && gameClient.getHabbo().getRoomUnit().getRoom() != null) {
            gameClient.getHabbo().getRoomUnit().getRoom().giveEffect(gameClient.getHabbo(), 140, 30);
            return true;
        }

        return false;
    }
}