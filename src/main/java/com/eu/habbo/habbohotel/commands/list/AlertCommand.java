package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AlertCommand extends Command {
    public AlertCommand() {
        super("cmd_alert");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_alert.forgot_username"), RoomChatMessageBubbles.ALERT);
            return true;
        }
        if (params.length < 3) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_alert.forgot_message"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        String targetUsername = params[1];
        String message = IntStream.range(2, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());

        Habbo habbo = getHabbo(targetUsername);

        if (habbo != null) {
            habbo.alert(message + "\r\n    -" + gameClient.getHabbo().getHabboInfo().getUsername());
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_alert.message_send"), targetUsername), RoomChatMessageBubbles.ALERT);
        } else {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_alert.user_offline"), targetUsername), RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}
