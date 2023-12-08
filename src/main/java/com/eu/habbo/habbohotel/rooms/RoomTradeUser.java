package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

public class RoomTradeUser {
    @Getter
    private final Habbo habbo;
    @Getter
    private final HashSet<RoomItem> items;
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
        this.items = new HashSet<>();
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

    public void putItemsIntoInventory() {
        this.habbo.getInventory().getItemsComponent().addItems(this.items);
    }

    public void clearItems() {
        this.items.clear();
    }
}

