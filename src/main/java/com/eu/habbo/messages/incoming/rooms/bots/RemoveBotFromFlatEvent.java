package com.eu.habbo.messages.incoming.rooms.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RemoveBotFromFlatEvent extends MessageHandler {


    @Override
    public void handle() {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null) {
            return;
        }

        int botId = this.packet.readInt();

        Bot bot = room.getRoomUnitManager().getRoomBotManager().getRoomBotById(Math.abs(botId));

        if(bot == null) {
            return;
        }

        room.getRoomUnitManager().getRoomBotManager().pickUpBot(bot, this.client.getHabbo());
    }
}
