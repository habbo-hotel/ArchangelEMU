package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.HabboBroadcastMessageComposer;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoomKickCommand extends Command {
    public RoomKickCommand() {
        super("cmd_room_kick");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        final Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null) {
            if (params.length > 1) {
                String message = IntStream.range(1, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());
                room.sendComposer(new HabboBroadcastMessageComposer(message + "\r\n-" + gameClient.getHabbo().getHabboInfo().getUsername()).compose());
            }

            for (Habbo habbo : room.getHabbos()) {
                if (!(habbo.hasRight(Permission.ACC_UNKICKABLE) || habbo.hasRight(Permission.ACC_SUPPORTTOOL) || room.isOwner(habbo))) {
                    room.kickHabbo(habbo, true);
                }
            }
        }
        return true;
    }
}
