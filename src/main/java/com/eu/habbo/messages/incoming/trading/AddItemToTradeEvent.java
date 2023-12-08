package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class AddItemToTradeEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getRoomUnit().getRoom() == null)
            return;

        RoomTrade trade = this.client.getHabbo().getRoomUnit().getRoom().getActiveTradeForHabbo(this.client.getHabbo());

        if (trade == null)
            return;

        RoomItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(this.packet.readInt());

        if (item == null || !item.getBaseItem().allowTrade())
            return;

        trade.offerItem(this.client.getHabbo(), item);
    }
}
