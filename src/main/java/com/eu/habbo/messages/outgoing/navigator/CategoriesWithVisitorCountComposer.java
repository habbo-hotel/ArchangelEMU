package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoriesWithVisitorCountComposer extends MessageComposer {
    private final List<RoomCategory> roomCategories;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.categoriesWithVisitorCountComposer);
        this.response.appendInt(this.roomCategories.size());

        for (int i = 0; i < this.roomCategories.size(); i++) {
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendInt(200);
        }
        return this.response;
    }
}