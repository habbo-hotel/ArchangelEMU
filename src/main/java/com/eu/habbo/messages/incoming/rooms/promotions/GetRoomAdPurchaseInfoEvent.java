package com.eu.habbo.messages.incoming.rooms.promotions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.promotions.RoomAdPurchaseInfoComposer;

import java.util.List;

public class GetRoomAdPurchaseInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        List<Room> roomsToShow = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo());
        roomsToShow.addAll(Emulator.getGameEnvironment().getRoomManager().getRoomsWithRights(this.client.getHabbo()));
        roomsToShow.addAll(Emulator.getGameEnvironment().getRoomManager().getRoomsWithAdminRights(this.client.getHabbo()));
        this.client.sendResponse(new RoomAdPurchaseInfoComposer(roomsToShow));
    }
}
