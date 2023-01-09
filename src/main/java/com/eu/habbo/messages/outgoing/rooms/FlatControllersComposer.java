package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class FlatControllersComposer extends MessageComposer {
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.flatControllersComposer);
        this.response.appendInt(this.room.getId());

        THashMap<Integer, String> rightsMap = this.room.getUsersWithRights();

        this.response.appendInt(rightsMap.size());

        for (Map.Entry<Integer, String> set : rightsMap.entrySet()) {
            this.response.appendInt(set.getKey());
            this.response.appendString(set.getValue());
        }

        return this.response;
    }
}
