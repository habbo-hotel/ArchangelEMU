package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionRentableSpace;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.UnseenItemsComposer;
import lombok.AllArgsConstructor;

import java.util.HashSet;

@AllArgsConstructor
public class ClearRentedSpace implements Runnable {
    private final InteractionRentableSpace item;
    private final Room room;

    @Override
    public void run() {
        HashSet<RoomItem> items = new HashSet<>();

        for (RoomTile t : this.room.getLayout().getTilesAt(this.room.getLayout().getTile(this.item.getCurrentPosition().getX(), this.item.getCurrentPosition().getY()), this.item.getBaseItem().getWidth(), this.item.getBaseItem().getLength(), this.item.getRotation())) {
            for (RoomItem i : this.room.getRoomItemManager().getItemsAt(t)) {
                if (i.getOwnerInfo().getId() == this.item.getRenterId()) {
                    items.add(i);
                    //Deprecated
                    i.setRoomId(0);
                    i.setRoom(null);
                    i.setSqlUpdateNeeded(true);
                }
            }
        }

        Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.item.getRenterId());

        if (owner != null) {
            owner.getClient().sendResponse(new UnseenItemsComposer(items));
            owner.getHabboStats().setRentedItemId(0);
            owner.getHabboStats().setRentedTimeEnd(0);
        } else {
            for (RoomItem i : items) {
                i.run();
            }
        }
    }
}
