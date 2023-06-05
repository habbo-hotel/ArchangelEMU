package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class DiagonalCommand extends Command {
    public DiagonalCommand() {
        super("cmd_diagonal");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasRights(gameClient.getHabbo())) {
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().moveDiagonally(!gameClient.getHabbo().getHabboInfo().getCurrentRoom().moveDiagonally());

            if (!gameClient.getHabbo().getHabboInfo().getCurrentRoom().moveDiagonally()) {
                gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_diagonal.disabled"), RoomChatMessageBubbles.ALERT);
            } else {
                gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_diagonal.enabled"), RoomChatMessageBubbles.ALERT);
            }

            return true;
        }

        return false;
    }
}