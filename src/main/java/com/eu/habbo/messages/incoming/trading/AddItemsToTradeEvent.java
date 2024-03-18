package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.trades.RoomTrade;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import gnu.trove.set.hash.THashSet;

public class AddItemsToTradeEvent extends TradingEvent {
    @Override
    public void handle() {
        if (this.client.getHabbo().getRoomUnit().getRoom() == null)
            return;

        RoomTrade trade = getActiveRoomTrade(this.client.getHabbo());

        if (trade == null)
            return;

        THashSet<RoomItem> items = new THashSet<>();

        int count = this.packet.readInt();
        for (int i = 0; i < count; i++) {
            RoomItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(this.packet.readInt());
            if (item != null && item.getBaseItem().allowTrade()) {
                items.add(item);
            }
        }

        trade.offerMultipleItems(this.client.getHabbo(), items);
    }
}
