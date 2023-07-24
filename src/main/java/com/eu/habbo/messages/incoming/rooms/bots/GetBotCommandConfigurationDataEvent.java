package com.eu.habbo.messages.incoming.rooms.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.BotCommandConfigurationComposer;

public class GetBotCommandConfigurationDataEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null)
            return;

        if (room.getRoomInfo().getOwnerInfo().getId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermissionRight(Permission.ACC_ANYROOMOWNER)) {
            int botId = this.packet.readInt();

            Bot bot = room.getRoomUnitManager().getRoomBotById(Math.abs(botId));

            if (bot == null)
                return;

            this.client.sendResponse(new BotCommandConfigurationComposer(bot, this.packet.readInt()));
        }
    }
}
