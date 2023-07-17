package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.items.interactions.InteractionSpinningBottle;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ThrowDiceEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null) {
            return;
        }

        RoomItem item = room.getHabboItem(itemId);

        if (item != null) {
            if (item instanceof InteractionDice || item instanceof InteractionSpinningBottle) {
                if (RoomLayout.tilesAdjecent(room.getLayout().getTile(item.getX(), item.getY()), this.client.getHabbo().getRoomUnit().getCurrentPosition())) {
                    item.onClick(this.client, room, new Object[]{});
                }
            }
        }
    }
}
