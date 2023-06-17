package com.eu.habbo.habbohotel.items.interactions.wired;

import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class WiredSettings implements IWiredSettings {
    @Getter
    @Setter
    private List<Integer> integerParams;

    @Getter
    @Setter
    private String stringParam;

    @Getter
    @Setter
    private List<Integer> itemIds;
    @Getter
    @Setter
    private int delay;

    @Getter
    @Setter
    private int selectionType;

    public WiredSettings() {
        this.itemIds = new ArrayList<>();
        this.integerParams = new ArrayList<>();
        this.stringParam = "";
        this.delay = 0;
        this.selectionType = 0;
    }

    public THashSet<HabboItem> getItems(Room room) {
        THashSet<HabboItem> items = new THashSet<>();

        for(int itemId : this.itemIds) {
            HabboItem item = room.getHabboItem(itemId);

            if(item == null || item.getRoomId() == 0) {
                this.itemIds.remove(itemId);
                continue;
            }

            items.add(item);
        }

        return items;
    }
}
