package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.hash.THashSet;
import lombok.AllArgsConstructor;

import java.util.NoSuchElementException;

@AllArgsConstructor
public class ObjectsMessageComposer extends MessageComposer {
    private final TIntObjectMap<String> furniOwnerNames;
    private final THashSet<? extends HabboItem> items;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.objectsMessageComposer);

        TIntObjectIterator<String> iterator = this.furniOwnerNames.iterator();

        this.response.appendInt(this.furniOwnerNames.size());
        for (int i = this.furniOwnerNames.size(); i-- > 0; ) {
            try {
                iterator.advance();
                this.response.appendInt(iterator.key());
                this.response.appendString(iterator.value());
            } catch (NoSuchElementException e) {
                break;
            }
        }

        this.response.appendInt(this.items.size());

        for (HabboItem item : this.items) {
            item.serializeFloorData(this.response);
            this.response.appendInt(item instanceof InteractionGift ? ((((InteractionGift) item).getColorId() * 1000) + ((InteractionGift) item).getRibbonId()) : (item instanceof InteractionMusicDisc ? ((InteractionMusicDisc) item).getSongId() : 1));
            item.serializeExtradata(this.response);
            this.response.appendInt(-1);
            this.response.appendInt(item instanceof InteractionTeleport || item instanceof InteractionSwitch || item instanceof InteractionSwitchRemoteControl || item instanceof InteractionVendingMachine || item instanceof InteractionInformationTerminal || item instanceof InteractionPostIt || item instanceof InteractionSpinningBottle || item instanceof InteractionPuzzleBox ? 2 : item.isUsable() ? 1 : 0);
            this.response.appendInt(item.getUserId());
        }
        return this.response;
    }
}
