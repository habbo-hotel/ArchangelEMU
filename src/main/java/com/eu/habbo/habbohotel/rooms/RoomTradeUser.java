package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;

public class RoomTradeUser {
    @Getter
    private final Habbo habbo;
    private final THashSet<RoomItem> items;
    @Setter
    @Getter
    private int userId;
    @Setter
    @Getter
    private boolean accepted;
    @Getter
    private boolean confirmed;

    public RoomTradeUser(Habbo habbo) {
        this.habbo = habbo;

        if (this.habbo != null) {
            this.userId = this.habbo.getHabboInfo().getId();
        }

        this.accepted = false;
        this.confirmed = false;
        this.items = new THashSet<>();
    }

    public void confirm() {
        this.confirmed = true;
    }

    public void addItem(RoomItem item) {
        this.items.add(item);
    }

    public RoomItem getItem(int itemId) {
        for (RoomItem item : this.items) {
            if (item.getId() == itemId) {
                return item;
            }
        }

        return null;
    }

    public THashSet<RoomItem> getItems() {
        return this.items;
    }

    public void putItemsIntoInventory() {
        this.habbo.getInventory().getItemsComponent().addItems(this.items);
    }

    public void clearItems() {
        this.items.clear();
    }
}

