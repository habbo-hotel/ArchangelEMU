package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ObjectUpdateMessageComposer extends MessageComposer {
    private final HabboItem item;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.objectUpdateMessageComposer);
        this.item.serializeFloorData(this.response);
        this.response.appendInt(this.item instanceof InteractionGift ? ((((InteractionGift) this.item).getColorId() * 1000) + ((InteractionGift) this.item).getRibbonId()) : (this.item instanceof InteractionMusicDisc ? ((InteractionMusicDisc) this.item).getSongId() : item.isUsable() ? 1 : 0));
        this.item.serializeExtradata(this.response);
        this.response.appendInt(-1);
        this.response.appendInt(0);
        this.response.appendInt(this.item.getUserId());
        return this.response;
    }
}
