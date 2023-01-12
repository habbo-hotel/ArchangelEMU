package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomForwardMessageComposer;
import com.eu.habbo.messages.outgoing.users.NavigatorSettingsComposer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForwardToSomeRoomEvent extends MessageHandler {
    @Override
    public void handle() {
        String data = this.packet.readString();

        if (data.equals("random_friending_room")) {
            List<Room> rooms = Emulator.getGameEnvironment().getRoomManager().getActiveRooms();
            if (!rooms.isEmpty()) {
                Collections.shuffle(rooms);
                this.client.sendResponse(new RoomForwardMessageComposer(rooms.get(0).getId()));
            }
        } else if (data.equalsIgnoreCase("predefined_noob_lobby")) {
            this.client.sendResponse(new RoomForwardMessageComposer(Emulator.getConfig().getInt("hotel.room.nooblobby")));
        } else {
            this.client.sendResponse(new NavigatorSettingsComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), this.client.getHabbo().getHabboInfo().getHomeRoom()));
        }
    }
}
