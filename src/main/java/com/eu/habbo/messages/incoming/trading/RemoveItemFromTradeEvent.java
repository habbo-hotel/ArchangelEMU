package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RemoveItemFromTradeEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        RoomTrade trade = this.client.getHabbo().getRoomUnit().getRoom().getActiveTradeForHabbo(this.client.getHabbo());
        if (trade != null) {
            RoomItem item = trade.getRoomTradeUserForHabbo(this.client.getHabbo()).getItem(itemId);

            if (!trade.getRoomTradeUserForHabbo(this.client.getHabbo()).isAccepted() && item != null) {
                trade.removeItem(this.client.getHabbo(), item);
            }
        }
    }
}
