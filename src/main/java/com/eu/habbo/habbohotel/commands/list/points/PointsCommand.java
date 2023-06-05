package com.eu.habbo.habbohotel.commands.list.points;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class PointsCommand extends BasePointsCommand {
    private static final String INVALID_AMOUNT_TEXTS = "commands.error.cmd_points.invalid_amount";

    public PointsCommand() {
        super("cmd_points");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 3) {
            gameClient.getHabbo().whisper(getTextsValue(INVALID_AMOUNT_TEXTS), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Habbo habbo = getHabbo(params[1]);

        if (habbo == null) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_points.user_offline"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        try {
            int type = Emulator.getConfig().getInt("seasonal.primary.type");
            int amount;
            if (params.length == 4) {
                try {
                    type = Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_points.invalid_type").replace("%types%", Emulator.getConfig().getValue("seasonal.types").replace(";", ", ")), RoomChatMessageBubbles.ALERT);
                    return true;
                }
            }

            try {
                amount = Integer.parseInt(params[2]);
            } catch (NumberFormatException e) {
                gameClient.getHabbo().whisper(getTextsValue(INVALID_AMOUNT_TEXTS), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (amount != 0) {
                habbo.givePoints(type, amount);

                if (habbo.getHabboInfo().getCurrentRoom() != null)
                    habbo.whisper(replaceAmountAndType(getTextsValue("commands.generic.cmd_points.received"), amount + "", getTextsValue("seasonal.name." + type)), RoomChatMessageBubbles.ALERT);
                else
                    habbo.alert(replaceAmountAndType(getTextsValue("commands.generic.cmd_points.received"), amount + "", getTextsValue("seasonal.name." + type)));

                gameClient.getHabbo().whisper(replaceUserAndAmountAndType(getTextsValue("commands.succes.cmd_points.send"), params[1], amount + "", getTextsValue("seasonal.name." + type)), RoomChatMessageBubbles.ALERT);

            } else {
                gameClient.getHabbo().whisper(getTextsValue(INVALID_AMOUNT_TEXTS), RoomChatMessageBubbles.ALERT);
            }
        } catch (NumberFormatException e) {
            gameClient.getHabbo().whisper(getTextsValue(INVALID_AMOUNT_TEXTS), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}
