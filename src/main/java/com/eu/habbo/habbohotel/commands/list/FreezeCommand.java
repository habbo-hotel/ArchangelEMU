package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class FreezeCommand extends Command {
    public FreezeCommand() {
        super("cmd_freeze");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 2) {
            Habbo habbo = getHabbo(params[1]);

            if (habbo == null) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_freeze.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
            } else {
                if (habbo.getRoomUnit().canWalk()) {
                    habbo.getRoomUnit().setCanWalk(false);
                    habbo.whisper(getTextsValue("commands.succes.cmd_freeze.frozen"), RoomChatMessageBubbles.ALERT);
                    gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_freeze.user_frozen"), params[1]), RoomChatMessageBubbles.ALERT);
                } else {
                    habbo.getRoomUnit().setCanWalk(true);
                    habbo.whisper(getTextsValue("commands.succes.cmd_freeze.unfrozen"), RoomChatMessageBubbles.ALERT);
                    gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_freeze.user_unfrozen"), params[1]), RoomChatMessageBubbles.ALERT);
                }
            }
            return true;
        } else {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_freeze.not_found"), ""), RoomChatMessageBubbles.ALERT);
            return true;
        }
    }
}
