package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.ListMode;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.incoming.MessageHandler;

public class NavigatorSetSearchCodeViewModeEvent extends MessageHandler {
    @Override
    public void handle() {
        String category = this.packet.readString();
        int viewMode = this.packet.readInt();

        RoomCategory roomCategory = Emulator.getGameEnvironment().getRoomManager().getCategory(category);
        this.client.getHabbo().getHabboStats().getNavigatorWindowSettings().setListMode(
                roomCategory != null ? roomCategory.getCaptionSave() : category, viewMode == 1 ? ListMode.THUMBNAILS : ListMode.LIST);
    }
}
