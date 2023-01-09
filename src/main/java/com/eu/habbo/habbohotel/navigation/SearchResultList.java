package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomState;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResultList implements ISerialize, Comparable<SearchResultList> {
    private  final int order;
    private  final String code;
    private  final String query;
    private  final SearchAction action;
    private  final ListMode mode;
    private  final DisplayMode hidden;
    private  final List<Room> rooms;
    private  final boolean filter;
    private  final boolean showInvisible;
    private  final DisplayOrder displayOrder;
    private  final int categoryOrder;

    @Override
    public void serialize(ServerMessage message) {
        message.appendString(this.code); //Search Code
        message.appendString(this.query); //Text
        message.appendInt(this.action.type); //Action Allowed (0 (Nothing), 1 (More Results), 2 (Go Back))
        message.appendBoolean(this.hidden.equals(DisplayMode.COLLAPSED)); //Closed
        message.appendInt(this.mode.getType()); //Display Mode (0 (List), 1 (Thumbnails), 2 (Thumbnail no choice))

        synchronized (this.rooms) {
            if (!this.showInvisible) {
                List<Room> toRemove = new ArrayList<>();
                for (Room room : this.rooms) {
                    if (room.getState() == RoomState.INVISIBLE) {
                        toRemove.add(room);
                    }
                }

                this.rooms.removeAll(toRemove);
            }

            message.appendInt(this.rooms.size());

            Collections.sort(this.rooms);
            for (Room room : this.rooms) {
                room.serialize(message);
            }
        }
    }

    @Override
    public int compareTo(SearchResultList o) {
        if (this.displayOrder == DisplayOrder.ACTIVITY) {
            if (this.code.equalsIgnoreCase("popular")) {
                return -1;
            }

            return this.rooms.size() - o.rooms.size();
        }
        return this.categoryOrder - o.categoryOrder;
    }
}