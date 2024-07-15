package com.eu.habbo.roleplay.messages.outgoing.items;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HotBarListItemsComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        THashMap<Integer, RoomItem> hotBarItems = this.habbo.getInventory().getHotBarComponent().getItems();
        this.response.init(Outgoing.hotBarListItemsComposer);
        this.response.appendInt(hotBarItems.size());
        for (RoomItem item : hotBarItems.values()) {
            this.response.appendString(item.getId()
                    + ";" + item.getBaseItem().getName()
            );
        }
        return this.response;
    }
}
