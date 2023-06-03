package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

public class FastwalkCommand extends Command {
    public FastwalkCommand() {
        super("cmd_fastwalk");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            //TODO Make this an event plugin which fires that can be cancelled
            if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboInfo().getRiding() != null)
                return true;

            Habbo habbo = gameClient.getHabbo();

            if (params.length >= 2) {
                String username = params[1];

                habbo = getHabbo(username);

                if (habbo == null)
                    return false;
            }
            habbo.getRoomUnit().setFastWalk(!habbo.getRoomUnit().isFastWalk());

            return true;
        }

        return false;
    }
}
