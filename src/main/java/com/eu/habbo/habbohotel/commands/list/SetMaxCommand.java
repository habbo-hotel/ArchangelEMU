package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class SetMaxCommand extends Command {
    public SetMaxCommand() {
        super("cmd_setmax");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length >= 2) {
            int max;
            try {
                max = Integer.parseInt(params[1]);
            } catch (Exception e) {
                return false;
            }

            if (max > 0 && max < 9999) {
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().setUsersMax(max);
                gameClient.getHabbo().whisper(getTextsValue("commands.success.cmd_setmax").replace("%value%", max + ""), RoomChatMessageBubbles.ALERT);
            } else {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_setmax.invalid_number"), RoomChatMessageBubbles.ALERT);
            }
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_setmax.forgot_number"), RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}