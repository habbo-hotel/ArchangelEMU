package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;

public class RoomSitCommand extends Command {
    public RoomSitCommand() {
        super("cmd_room_sit");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos().forEach(habbo -> {
            if (habbo.getRoomUnit().isWalking()) {
                habbo.getRoomUnit().stopWalking();
            } else if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.SIT)) {
                return;
            }
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().makeSit(habbo);
        });

        return true;
    }
}
