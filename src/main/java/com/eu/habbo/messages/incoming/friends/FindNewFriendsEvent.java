package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FindFriendsProcessResultComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomForwardMessageComposer;

import java.util.Collections;
import java.util.List;

public class FindNewFriendsEvent extends MessageHandler {
    @Override
    public void handle() {
        List<RoomCategory> roomCategories = Emulator.getGameEnvironment().getRoomManager().roomCategoriesForHabbo(this.client.getHabbo());
        Collections.shuffle(roomCategories);

        for (RoomCategory category : roomCategories) {
            List<Room> rooms = Emulator.getGameEnvironment().getRoomManager().getActiveRooms(category.getId());

            if (!rooms.isEmpty()) {
                Room room = rooms.get(0);

                if (room.getUserCount() > 0) {
                    this.client.sendResponse(new RoomForwardMessageComposer(room.getId()));
                    return;
                }
            }
        }

        this.client.sendResponse(new FindFriendsProcessResultComposer(FindFriendsProcessResultComposer.NO_ROOM_FOUND));
    }
}
