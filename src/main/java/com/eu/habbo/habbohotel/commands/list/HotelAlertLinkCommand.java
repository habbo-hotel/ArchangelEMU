package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.ModeratorMessageComposer;

public class HotelAlertLinkCommand extends Command {
    public HotelAlertLinkCommand() {
        super("cmd_hal");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 3) {
            return true;
        }

        String url = params[1];
        StringBuilder message = new StringBuilder();
        for (int i = 2; i < params.length; i++) {
            message.append(params[i]);
            message.append(" ");
        }

        message.append("\r\r-<b>").append(gameClient.getHabbo().getHabboInfo().getUsername()).append("</b>");

        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new ModeratorMessageComposer(message.toString(), url).compose());
        return true;
    }
}