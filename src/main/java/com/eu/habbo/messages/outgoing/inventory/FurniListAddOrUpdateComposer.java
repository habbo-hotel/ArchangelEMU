package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FurniListAddOrUpdateComposer extends MessageComposer {
    private final RoomItem roomItem;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.furniListAddOrUpdateComposer);
        this.response.appendInt(this.roomItem.getGiftAdjustedId());
        this.response.appendString(this.roomItem.getBaseItem().getType().code);
        this.response.appendInt(this.roomItem.getId());
        this.response.appendInt(this.roomItem.getBaseItem().getSpriteId());

        switch (this.roomItem.getBaseItem().getName()) {
            case "landscape" -> this.response.appendInt(4);
            case "floor" -> this.response.appendInt(3);
            case "wallpaper" -> this.response.appendInt(2);
            case "poster" -> this.response.appendInt(6);
        }

        if (this.roomItem.isLimited()) {
            this.response.appendInt(1);
            this.response.appendInt(256);
            this.response.appendString(this.roomItem.getExtraData());
            this.response.appendInt(this.roomItem.getLimitedSells());
            this.response.appendInt(this.roomItem.getLimitedStack());
        } else {
            this.response.appendInt(1);
            this.response.appendInt(0);
            this.response.appendString(this.roomItem.getExtraData());
        }
        this.response.appendBoolean(this.roomItem.getBaseItem().allowRecyle());
        this.response.appendBoolean(this.roomItem.getBaseItem().allowTrade());
        this.response.appendBoolean(!this.roomItem.isLimited() && this.roomItem.getBaseItem().allowInventoryStack());
        this.response.appendBoolean(this.roomItem.getBaseItem().allowMarketplace());
        this.response.appendInt(-1);
        this.response.appendBoolean(false);
        this.response.appendInt(-1);

        if (this.roomItem.getBaseItem().getType() == FurnitureType.FLOOR) {
            this.response.appendString(""); //slotId
            this.response.appendInt(0);
        }
        this.response.appendInt(100);
        return this.response;
    }
}
