package com.eu.habbo.habbohotel.items.interactions.wired;

import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
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

    public HashSet<RoomItem> getItems(Room room) {
        HashSet<RoomItem> items = new HashSet<>();

        if(this.itemIds.size() == 0) {
            return items;
        }

        for(int i = 0; i < this.itemIds.size(); i++) {
            RoomItem item = room.getRoomItemManager().getRoomItemById(this.itemIds.get(i));

            if(item == null || item.getRoomId() == 0) {
                this.itemIds.remove(i);
                if(!matchParams.isEmpty()) {
                    this.matchParams.removeIf(setting -> setting.getItem_id() == item.getId());
                }
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
