package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.trades.RoomTrade;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;

public class AddItemToTradeEvent extends TradingEvent {
    @Override
    public void handle() {
        if (this.client.getHabbo().getRoomUnit().getRoom() == null)
            return;

        RoomTrade trade = getActiveRoomTrade(this.client.getHabbo());

        if (trade == null)
            return;

        RoomItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(this.packet.readInt());

        if (item == null || !item.getBaseItem().allowTrade())
            return;

        trade.offerItem(this.client.getHabbo(), item);
    }
}
