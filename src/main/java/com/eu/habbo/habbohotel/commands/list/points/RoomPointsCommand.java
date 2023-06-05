package com.eu.habbo.habbohotel.commands.list.points;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class RoomPointsCommand extends BasePointsCommand {
    public RoomPointsCommand() {
        super("cmd_room_points");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        int type = Emulator.getConfig().getInt("seasonal.primary.type");
        String amountString;
        if (params.length == 3) {
            try {
                amountString = params[1];

                type = Integer.parseInt(params[2]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_masspoints.invalid_type").replace("%types%", Emulator.getConfig().getValue("seasonal.types").replace(";", ", ")), RoomChatMessageBubbles.ALERT);
                return true;
            }
        } else if (params.length == 2) {
            amountString = params[1];
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_masspoints.invalid_amount"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        boolean found = false;
        for (String s : Emulator.getConfig().getValue("seasonal.types").split(";")) {
            if (s.equalsIgnoreCase(type + "")) {
                found = true;
                break;
            }
        }

        if (!found) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_masspoints.invalid_type").replace("%types%", Emulator.getConfig().getValue("seasonal.types").replace(";", ", ")), RoomChatMessageBubbles.ALERT);
            return true;
        }

        int amount;

        try {
            amount = Integer.parseInt(amountString);
        } catch (Exception e) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_masspoints.invalid_amount"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (amount != 0) {
            final String message = replaceAmountAndType(getTextsValue("commands.generic.cmd_points.received"), amount + "", getTextsValue("seasonal.name." + type));

            for (Habbo habbo : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos()) {
                habbo.givePoints(type, amount);
                habbo.whisper(message, RoomChatMessageBubbles.ALERT);
            }
        }
        return true;
    }
}