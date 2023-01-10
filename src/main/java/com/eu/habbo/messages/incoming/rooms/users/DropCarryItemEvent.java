package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;

public class DropCarryItemEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        this.client.getHabbo().getRoomUnit().setHandItem(0);
        if (room != null) {
            room.unIdle(this.client.getHabbo());
            room.sendComposer(new CarryObjectMessageComposer(this.client.getHabbo().getRoomUnit()).compose());
        }
    }
}
