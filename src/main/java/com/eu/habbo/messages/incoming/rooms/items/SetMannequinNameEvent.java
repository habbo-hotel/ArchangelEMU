package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class SetMannequinNameEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();
        if (room == null || !room.getRoomInfo().isRoomOwner(this.client.getHabbo()))
            return;

        int id = this.packet.readInt();
        RoomItem item = room.getRoomItemManager().getRoomItemById(id);
        if (item == null)
            return;

        String[] data = item.getExtraData().split(":");
        String name = this.packet.readString();

        if (name.length() < 3 || name.length() > 15) {
            name = Emulator.getTexts().getValue("hotel.mannequin.name.default", "My look");
        }

        if (data.length == 3) {
            item.setExtraData(this.client.getHabbo().getHabboInfo().getGender().name().toUpperCase() + ":" + data[1] + ":" + name);
        } else {
            item.setExtraData(this.client.getHabbo().getHabboInfo().getGender().name().toUpperCase() + ":" + this.client.getHabbo().getHabboInfo().getLook() + ":" + name);
        }
        item.setSqlUpdateNeeded(true);
        Emulator.getThreading().run(item);
        room.updateItem(item);
    }
}
