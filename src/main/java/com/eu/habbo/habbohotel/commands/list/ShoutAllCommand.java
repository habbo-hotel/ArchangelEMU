package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShoutAllCommand extends Command {
    public ShoutAllCommand() {
        super("cmd_shout_all");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_shout_all.forgot_message"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        String message = IntStream.range(1, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());

        for (Habbo habbo : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos()) {
            habbo.shout(message);
        }

        return true;
    }
}
