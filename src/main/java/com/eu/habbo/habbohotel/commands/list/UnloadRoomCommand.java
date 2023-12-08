package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;

public class UnloadRoomCommand extends Command {
    public UnloadRoomCommand() {
        super("cmd_unload");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Room room = gameClient.getHabbo().getRoomUnit().getRoom();

        if (room != null && (room.getRoomInfo().isRoomOwner(gameClient.getHabbo()) || (room.getRoomInfo().hasGuild() && room.getGuildRightLevel(gameClient.getHabbo()).equals(RoomRightLevels.GUILD_ADMIN))) || gameClient.getHabbo().hasPermissionRight(Permission.ACC_ANYROOMOWNER)) {
            room.dispose();
            return true;
        }

        return false;
    }
}
