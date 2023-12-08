package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class DiceOffEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null)
            return;

        RoomItem item = room.getRoomItemManager().getRoomItemById(itemId);

        if (item != null) {
            if (item instanceof InteractionDice) {
                if (RoomLayout.tilesAdjecent(room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()), this.client.getHabbo().getRoomUnit().getCurrentPosition())) {
                    if (!item.getExtraData().equals("-1")) {
                        item.setExtraData("0");
                        item.setSqlUpdateNeeded(true);
                        Emulator.getThreading().run(item);
                        room.updateItem(item);
                    }
                }
            } else {
                ScripterManager.scripterDetected(this.client, Emulator.getTexts().getValue("scripter.warning.packet.closedice").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%id%", item.getId() + "").replace("%itemname%", item.getBaseItem().getName()));
            }
        }
    }
}
