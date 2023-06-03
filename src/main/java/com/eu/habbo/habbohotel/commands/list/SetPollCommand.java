package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class SetPollCommand extends Command {
    public SetPollCommand() {
        super("cmd_set_poll");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_set_poll.missing_arg"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            int pollId = -1;
            try {
                pollId = Integer.parseInt(params[1]);
            } catch (Exception ignored) {}

            if (pollId >= 0) {
                if (Emulator.getGameEnvironment().getPollManager().getPoll(pollId) != null) {
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().setPollId(pollId);
                    gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_set_poll"), RoomChatMessageBubbles.ALERT);
                } else {
                    gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_set_poll.not_found"), RoomChatMessageBubbles.ALERT);
                }
            } else {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_set_poll.invalid_number"), RoomChatMessageBubbles.ALERT);
            }
        }

        return true;
    }
}