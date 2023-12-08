package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class SetMannequinFigureEvent extends MessageHandler {
    @Override
    public void handle() {
        Habbo habbo = this.client.getHabbo();
        Room room = habbo.getRoomUnit().getRoom();

        if (room == null || !room.getRoomInfo().isRoomOwner(habbo))
            return;

        int id = this.packet.readInt();
        RoomItem item = room.getRoomItemManager().getRoomItemById(id);
        if (item == null)
            return;

        String[] data = item.getExtraData().split(":");
        //TODO: Only clothing not whole body part.

        StringBuilder look = new StringBuilder();

        for (String s : habbo.getHabboInfo().getLook().split("\\.")) {
            if (!s.contains("hr") && !s.contains("hd") && !s.contains("he") && !s.contains("ea") && !s.contains("ha") && !s.contains("fa")) {
                look.append(s).append(".");
            }
        }

        if (look.length() > 0) {
            look = new StringBuilder(look.substring(0, look.length() - 1));
        }

        if (data.length == 3) {
            item.setExtraData(habbo.getHabboInfo().getGender().name().toLowerCase() + ":" + look + ":" + data[2]);
        } else {
            item.setExtraData(habbo.getHabboInfo().getGender().name().toLowerCase() + ":" + look + ":" + habbo.getHabboInfo().getUsername() + "'s look.");
        }

        item.setSqlUpdateNeeded(true);
        Emulator.getThreading().run(item);
        room.updateItem(item);
    }
}
