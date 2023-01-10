package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootballGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class SetClothingChangeDataEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null || this.client.getHabbo().getHabboInfo().getId() != room.getOwnerId())
            return;

        HabboItem item = room.getHabboItem(this.packet.readInt());
        if (!(item instanceof InteractionFootballGate))
            return;

        String gender = this.packet.readString();
        String look = this.packet.readString();

        switch (gender.toLowerCase()) {
            case "m" -> {
                ((InteractionFootballGate) item).setFigureM(look);
                room.updateItem(item);
            }
            case "f" -> {
                ((InteractionFootballGate) item).setFigureF(look);
                room.updateItem(item);
            }
        }
    }
}