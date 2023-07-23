package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootballGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class SetClothingChangeDataEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null || this.client.getHabbo().getHabboInfo().getId() != room.getRoomInfo().getOwnerInfo().getId())
            return;

        int id = this.packet.readInt();
        RoomItem item = room.getRoomItemManager().getRoomItemById(id);
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