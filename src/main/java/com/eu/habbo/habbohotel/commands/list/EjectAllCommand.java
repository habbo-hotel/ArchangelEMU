package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;

public class EjectAllCommand extends Command {
    public EjectAllCommand() {
        super("cmd_ejectall");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null && (room.isOwner(gameClient.getHabbo())
                || (room.hasGuild() && room.getGuildRightLevel(gameClient.getHabbo()).equals(RoomRightLevels.GUILD_ADMIN)))) {
            room.ejectAll(gameClient.getHabbo());
        }

        return true;
    }
}
