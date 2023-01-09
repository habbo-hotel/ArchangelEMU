package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;

public class RoomTradeUser {
    @Getter
    private final Habbo habbo;
    private final THashSet<HabboItem> items;
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

    public void addItem(HabboItem item) {
        this.items.add(item);
    }

    public HabboItem getItem(int itemId) {
        for (HabboItem item : this.items) {
            if (item.getId() == itemId) {
                return item;
            }
        }

        return null;
    }

    public THashSet<HabboItem> getItems() {
        return this.items;
    }

    public void putItemsIntoInventory() {
        this.habbo.getInventory().getItemsComponent().addItems(this.items);
    }

    public void clearItems() {
        this.items.clear();
    }
}

