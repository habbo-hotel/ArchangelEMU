package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureToggleEvent;

public class UseWallItemEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null)
            return;

        int itemId = this.packet.readInt();
        int state = this.packet.readInt();

        RoomItem item = room.getRoomItemManager().getRoomItemById(itemId);

        if (item == null)
            return;

        Event furnitureToggleEvent = new FurnitureToggleEvent(item, this.client.getHabbo(), state);
        Emulator.getPluginManager().fireEvent(furnitureToggleEvent);

        if (furnitureToggleEvent.isCancelled())
            return;

        if (item.getBaseItem().getName().equalsIgnoreCase("poster"))
            return;

        item.setSqlUpdateNeeded(true);
        item.onClick(this.client, room, new Object[]{state});
        room.updateItem(item);
        Emulator.getThreading().run(item);
    }
}
