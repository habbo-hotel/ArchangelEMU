package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.ClothItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

public class FigureSetIdsComposer extends MessageComposer {
    private final ArrayList<Integer> idList = new ArrayList<>();
    private final ArrayList<String> nameList = new ArrayList<>();

    public FigureSetIdsComposer(Habbo habbo) {
        habbo.getInventory().getWardrobeComponent().getClothing().forEach(value -> {
            ClothItem item = Emulator.getGameEnvironment().getCatalogManager().clothing.get(value);

            if (item != null) {
                for (Integer j : item.getSetId()) {
                    FigureSetIdsComposer.this.idList.add(j);
                }

                FigureSetIdsComposer.this.nameList.add(item.getName());
            }

            return true;
        });
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.figureSetIdsComposer);
        this.response.appendInt(this.idList.size());
        this.idList.forEach(this.response::appendInt);
        this.response.appendInt(this.nameList.size());
        this.nameList.forEach(this.response::appendString);
        return this.response;
    }
}
