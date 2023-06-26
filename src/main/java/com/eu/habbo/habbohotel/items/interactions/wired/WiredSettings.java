package com.eu.habbo.habbohotel.items.interactions.wired;

import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class WiredSettings implements IWiredSettings {
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Integer> integerParams;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String stringParam;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Integer> itemIds;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int delay;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<WiredMatchFurniSetting> matchParams;

    @Getter
    @Setter
    @JsonIgnore
    private int selectionType;

    public WiredSettings() {
        this.itemIds = new ArrayList<>();
        this.integerParams = new ArrayList<>();
        this.stringParam = "";
        this.delay = 0;
        this.matchParams = new ArrayList<>();
        this.selectionType = 0;
    }

    public THashSet<HabboItem> getItems(Room room) {
        THashSet<HabboItem> items = new THashSet<>();

        if(this.itemIds.size() == 0) {
            return items;
        }

        for(int i = 0; i < this.itemIds.size(); i++) {
            HabboItem item = room.getHabboItem(this.itemIds.get(i));

            if(item == null || item.getRoomId() == 0) {
                this.itemIds.remove(i);
                continue;
            }

            items.add(item);
        }

        return items;
    }

    public void dispose() {
        this.integerParams.clear();
        this.itemIds.clear();
        this.stringParam = "";
        this.delay = 0;
        this.matchParams.clear();
        this.selectionType = 0;
    }
}
