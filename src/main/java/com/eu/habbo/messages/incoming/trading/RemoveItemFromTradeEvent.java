package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.trades.RoomTrade;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;

public class RemoveItemFromTradeEvent extends TradingEvent {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        RoomTrade trade = getActiveRoomTrade(this.client.getHabbo());
        if (trade != null) {
            RoomItem item = trade.getRoomTradeUserForHabbo(this.client.getHabbo()).getItem(itemId);

            if (!trade.getRoomTradeUserForHabbo(this.client.getHabbo()).isAccepted() && item != null) {
                trade.removeItem(this.client.getHabbo(), item);
            }
        }
    }
}
