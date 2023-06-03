package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class MoonwalkCommand extends Command {
    public MoonwalkCommand() {
        super("cmd_moonwalk");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboStats().hasActiveClub()) {
            int effect = 136;
            if (gameClient.getHabbo().getRoomUnit().getEffectId() == 136)
                effect = 0;

            gameClient.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(gameClient.getHabbo(), effect, -1);

            return true;
        }

        return false;
    }
}
