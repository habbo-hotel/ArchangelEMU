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
        Room room = gameClient.getHabbo().getRoomUnit().getRoom();

        if (room != null) {
            if (room.getRoomInfo().isRoomOwner(gameClient.getHabbo())
                    || (room.getRoomInfo().hasGuild() && room.getGuildRightLevel(gameClient.getHabbo()).equals(RoomRightLevels.GUILD_ADMIN))) {
                room.ejectAllFurni(gameClient.getHabbo());
            }
        }

        return true;
    }
}
