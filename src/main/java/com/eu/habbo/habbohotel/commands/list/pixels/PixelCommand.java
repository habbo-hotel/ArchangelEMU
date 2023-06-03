package com.eu.habbo.habbohotel.commands.list.pixels;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class PixelCommand extends BasePixelsCommand {
    public PixelCommand() {
        super("cmd_duckets");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length != 3) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_duckets.invalid_amount"), RoomChatMessageBubbles.ALERT);
            return true;
        }
        Habbo habbo = getHabbo(params[1]);

        if (habbo != null) {
            try {
                if (Integer.parseInt(params[2]) != 0) {
                    habbo.givePixels(Integer.parseInt(params[2]));
                    if (habbo.getHabboInfo().getCurrentRoom() != null)
                        habbo.whisper(replaceAmount(getTextsValue("commands.generic.cmd_duckets.received"), params[2]), RoomChatMessageBubbles.ALERT);
                    else
                        habbo.alert(replaceAmount(getTextsValue("commands.generic.cmd_duckets.received"), params[2]));

                    gameClient.getHabbo().whisper(replaceUserAndAmount(getTextsValue("commands.succes.cmd_duckets.send"), params[1], params[2]), RoomChatMessageBubbles.ALERT);

                } else {
                    gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_duckets.invalid_amount"), RoomChatMessageBubbles.ALERT);
                }
            } catch (NumberFormatException e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_duckets.invalid_amount"), RoomChatMessageBubbles.ALERT);
            }
        } else {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_duckets.user_offline"), params[1]), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}