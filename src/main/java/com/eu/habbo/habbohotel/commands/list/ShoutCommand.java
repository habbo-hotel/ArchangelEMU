package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.ShoutMessageComposer;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShoutCommand extends Command {
    public ShoutCommand() {
        super("cmd_shout");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_shout.forgot_username"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

        if (target == null) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_shout.user_not_found"), RoomChatMessageBubbles.ALERT);
            return true;
        } else {
            if (target.getHabboInfo().getCurrentRoom() == null) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_shout.hotel_view"), params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }
        }

        String message = "";
        if (params.length > 2) {
            message = IntStream.range(2, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());
        }

        target.getHabboInfo().getCurrentRoom().sendComposer(new ShoutMessageComposer(new RoomChatMessage(message, target, RoomChatMessageBubbles.NORMAL)).compose());
        gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_shout"), params[1]).replace("%message%", message), RoomChatMessageBubbles.ALERT);
        return true;
    }
}
