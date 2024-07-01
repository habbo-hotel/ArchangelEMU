package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.items.interactions.InteractionSpinningBottle;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ThrowDiceEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null) {
            return;
        }

        RoomItem item = room.getRoomItemManager().getRoomItemById(itemId);

        if (item != null) {
            if (item instanceof InteractionDice || item instanceof InteractionSpinningBottle) {
                if (RoomLayout.tilesAdjacent(room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()), this.client.getHabbo().getRoomUnit().getCurrentPosition())) {
                    item.onClick(this.client, room, new Object[]{});
                }
            }
        }
    }
}
