package com.eu.habbo.plugin.events.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.plugin.events.users.UserEvent;
import gnu.trove.set.hash.THashSet;

public class RoomFloorItemsLoadEvent extends UserEvent {
    private THashSet<RoomItem> floorItems;
    private boolean changedFloorItems;

    public RoomFloorItemsLoadEvent(Habbo habbo, THashSet<RoomItem> floorItems) {
        super(habbo);
        this.floorItems = floorItems;
        this.changedFloorItems = false;
    }

    public void setFloorItems(THashSet<RoomItem> floorItems) {
        this.changedFloorItems = true;
        this.floorItems = floorItems;
    }

    public boolean hasChangedFloorItems() {
        return this.changedFloorItems;
    }

    public THashSet<RoomItem> getFloorItems() {
        return this.floorItems;
    }
}
