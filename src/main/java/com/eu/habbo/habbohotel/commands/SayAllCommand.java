package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SayAllCommand extends Command {
    public SayAllCommand() {
        super("cmd_say_all", Emulator.getTexts().getValue("commands.keys.cmd_say_all").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_say_all.forgot_message"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        String message = IntStream.range(1, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());

        gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos().forEach(habbo -> habbo.talk(message));

        return true;
    }
}
