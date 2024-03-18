package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class FurniListComposer extends MessageComposer implements TIntObjectProcedure<RoomItem> {

    private final int fragmentNumber;
    private final int totalFragments;
    private final TIntObjectMap<RoomItem> items;


    @Override
    protected ServerMessage composeInternal() {
        try {
            this.response.init(Outgoing.furniListComposer);
            this.response.appendInt(this.totalFragments);
            this.response.appendInt(this.fragmentNumber - 1);
            this.response.appendInt(this.items.size());

            this.items.forEachEntry(this);
            return this.response;
        } catch (Exception e) {
            log.error("Caught exception", e);
        }

        return null;
    }

    @Override
    public boolean execute(int a, RoomItem roomItem) {
        this.response.appendInt(roomItem.getGiftAdjustedId());
        this.response.appendString(roomItem.getBaseItem().getType().code);
        this.response.appendInt(roomItem.getId());
        this.response.appendInt(roomItem.getBaseItem().getSpriteId());

        if (roomItem.getBaseItem().getName().equals("floor") || roomItem.getBaseItem().getName().equals("song_disk") || roomItem.getBaseItem().getName().equals("landscape") || roomItem.getBaseItem().getName().equals("wallpaper") || roomItem.getBaseItem().getName().equals("poster")) {
            switch (roomItem.getBaseItem().getName()) {
                case "landscape" -> this.response.appendInt(4);
                case "floor" -> this.response.appendInt(3);
                case "wallpaper" -> this.response.appendInt(2);
                case "poster" -> this.response.appendInt(6);
                case "song_disk" -> this.response.appendInt(8);
            }

            this.response.appendInt(0);
            this.response.appendString(roomItem.getExtraData());
        } else {
            if (roomItem.getBaseItem().getName().equals("gnome_box"))
                this.response.appendInt(13);
            else
                this.response.appendInt(roomItem instanceof InteractionGift ? ((((InteractionGift) roomItem).getColorId() * 1000) + ((InteractionGift) roomItem).getRibbonId()) : 1);

            roomItem.serializeExtradata(this.response);
        }
        this.response.appendBoolean(roomItem.getBaseItem().allowRecyle());
        this.response.appendBoolean(roomItem.getBaseItem().allowTrade());
        this.response.appendBoolean(!roomItem.isLimited() && roomItem.getBaseItem().allowInventoryStack());
        this.response.appendBoolean(roomItem.getBaseItem().allowMarketplace());
        this.response.appendInt(-1);
        this.response.appendBoolean(true);
        this.response.appendInt(-1);

        if (roomItem.getBaseItem().getType() == FurnitureType.FLOOR) {
            this.response.appendString("");
            if(roomItem.getBaseItem().getName().equals("song_disk")) {
                List<String> extraDataAsList = Arrays.asList(roomItem.getExtraData().split("\n"));
                this.response.appendInt(Integer.valueOf(extraDataAsList.get(extraDataAsList.size() - 1)));
                return true;
            }
            this.response.appendInt(roomItem instanceof InteractionGift ? ((((InteractionGift) roomItem).getColorId() * 1000) + ((InteractionGift) roomItem).getRibbonId()) : 1);
        }

        return true;
    }

    public void addExtraDataToResponse(RoomItem roomItem) {
        this.response.appendInt(0);
        this.response.appendString(roomItem.getExtraData());
    }

}
