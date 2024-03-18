package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.UserFlatCatsComposer;

import java.util.List;

public class GetUserFlatCatsEvent extends MessageHandler {
    @Override
    public void handle() {
        List<RoomCategory> roomCategoryList = Emulator.getGameEnvironment().getRoomManager().roomCategoriesForHabbo(this.client.getHabbo());
        this.client.sendResponse(new UserFlatCatsComposer(roomCategoryList));
        //this.client.sendResponse(new NewNavigatorEventCategoriesComposer());
    }
}
