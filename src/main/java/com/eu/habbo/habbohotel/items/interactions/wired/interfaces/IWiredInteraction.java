package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

public interface IWiredInteraction {
    THashSet<HabboItem> getItems();
    void setItems(THashSet<HabboItem> value);
    String getWiredData();
    void setWiredData(String value);
    WiredSettings getWiredSettings();
    void setWiredSettings(WiredSettings value);
    boolean execute(RoomUnit roomUnit, Room room, Object[] stuff);
    boolean saveData() throws WiredSaveException;
}
