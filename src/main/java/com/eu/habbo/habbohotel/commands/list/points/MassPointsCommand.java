package com.eu.habbo.habbohotel.commands.list.points;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class MassPointsCommand extends BasePointsCommand {
    public MassPointsCommand() {
        super("cmd_mass_points");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        int type = Emulator.getConfig().getInt("seasonal.primary.type");
        String amountString;
        if (params.length == 3) {
            amountString = params[1];
            try {
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
            String message = replaceAmountAndType(getTextsValue("commands.generic.cmd_points.received"), amount + "", Emulator.getTexts().getValue("seasonal.name." + type));

            for (Habbo habbo : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()) {
                habbo.givePoints(type, amount);

                if (habbo.getHabboInfo().getCurrentRoom() != null)
                    habbo.whisper(message, RoomChatMessageBubbles.ALERT);
                else
                    habbo.alert(message);
            }
        }
        return true;
    }
}
