package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.rooms.RoomTradeUser;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class TradeUpdateComposer extends MessageComposer {
    private final RoomTrade roomTrade;

    public TradeUpdateComposer(RoomTrade roomTrade) {
        this.roomTrade = roomTrade;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.TradeUpdateComposer);
        for (RoomTradeUser roomTradeUser : this.roomTrade.getRoomTradeUsers()) {
            this.response.appendInt(roomTradeUser.getUserId());

            this.response.appendInt(roomTradeUser.getItems().size());
            for (HabboItem item : roomTradeUser.getItems()) {
                this.response.appendInt(item.getId());
                this.response.appendString(item.getBaseItem().getType().code);
                this.response.appendInt(item.getId());
                this.response.appendInt(item.getBaseItem().getSpriteId());
                this.response.appendInt(0);
                this.response.appendBoolean(item.getBaseItem().allowInventoryStack() && !item.isLimited());
                item.serializeExtradata(this.response);
                this.response.appendInt(0);
                this.response.appendInt(0);
                this.response.appendInt(0);

                if (item.getBaseItem().getType() == FurnitureType.FLOOR)
                    this.response.appendInt(0);
            }

            this.response.appendInt(roomTradeUser.getItems().size());
            this.response.appendInt(roomTradeUser.getItems().stream().mapToInt(this::getCreditsByItem).sum());
        }
        return this.response;
    }

    private int getCreditsByItem(HabboItem item) {
        if (!item.getBaseItem().getName().startsWith("CF_") && !item.getBaseItem().getName().startsWith("CFC_")) return 0;

        try {
            return Integer.valueOf(item.getBaseItem().getName().split("_")[1]);
        } catch (Exception e) {
            return 0;
        }
    }
}
