package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ObjectAddMessageComposer extends MessageComposer {
    private final HabboItem item;
    private final String itemOwnerName;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.objectAddMessageComposer);
        this.item.serializeFloorData(this.response);
        this.response.appendInt(this.item instanceof InteractionGift ? ((((InteractionGift) this.item).getColorId() * 1000) + ((InteractionGift) this.item).getRibbonId()) : (this.item instanceof InteractionMusicDisc ? ((InteractionMusicDisc) this.item).getSongId() : 1));
        this.item.serializeExtradata(this.response);
        this.response.appendInt(-1);
        this.response.appendInt(this.item instanceof InteractionTeleport || this.item instanceof InteractionSwitch || this.item instanceof InteractionSwitchRemoteControl || this.item instanceof InteractionVendingMachine || this.item instanceof InteractionInformationTerminal || this.item instanceof InteractionPostIt || this.item instanceof InteractionSpinningBottle || this.item instanceof InteractionPuzzleBox ? 2 : this.item.isUsable() ? 1 : 0);
        this.response.appendInt(this.item.getUserId());
        this.response.appendString(this.itemOwnerName);
        return this.response;
    }
}
