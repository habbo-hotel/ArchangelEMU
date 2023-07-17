package com.eu.habbo.messages.incoming.rooms.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RemoveBotFromFlatEvent extends MessageHandler {


    @Override
    public void handle() {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null)
            return;

        Emulator.getGameEnvironment().getBotManager().pickUpBot(this.packet.readInt(), this.client.getHabbo(), room);
    }
}
