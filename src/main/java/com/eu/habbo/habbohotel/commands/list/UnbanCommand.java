package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UnbanCommand extends Command {
    public UnbanCommand() {
        super("cmd_unban");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 1) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_unban.not_specified"), RoomChatMessageBubbles.ALERT);
        } else {
            if (Emulator.getGameEnvironment().getModToolManager().unban(params[1])) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_unban.success"), params[1]), RoomChatMessageBubbles.ALERT);
            } else {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_unban.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
            }
        }

        return true;
    }
}
