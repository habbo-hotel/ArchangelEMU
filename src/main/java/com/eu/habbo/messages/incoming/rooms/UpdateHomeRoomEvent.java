package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.NavigatorSettingsComposer;

public class UpdateHomeRoomEvent extends MessageHandler {
    @Override
    public void handle() {
        int roomId = this.packet.readInt();

        if (roomId != this.client.getHabbo().getHabboInfo().getHomeRoom()) {
            this.client.getHabbo().getHabboInfo().setHomeRoom(roomId);
            this.client.sendResponse(new NavigatorSettingsComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), 0));
        }
    }
}
