package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.RoomForwardMessageComposer;

import java.util.ArrayList;
import java.util.Collection;

public class ReloadRoomCommand extends Command {
    public ReloadRoomCommand() {
        super("cmd_reload_room");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getThreading().run(() -> {
            Room room = gameClient.getHabbo().getRoomUnit().getRoom();
            if (room != null) {
                Collection<Habbo> habbos = new ArrayList<>(room.getRoomUnitManager().getCurrentHabbos().values());
                room.dispose();
                room = Emulator.getGameEnvironment().getRoomManager().getRoom(room.getRoomInfo().getId());
                ServerMessage message = new RoomForwardMessageComposer(room.getRoomInfo().getId()).compose();
                habbos.forEach(habbo -> habbo.getClient().sendResponse(message));
            }
        }, 100);

        return true;
    }
}