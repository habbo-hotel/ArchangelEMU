package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.messenger.Message;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.friends.NewConsoleMessageComposer;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StaffAlertCommand extends Command {
    public StaffAlertCommand() {
        super("cmd_staff_alert");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length > 1) {
            String message = IntStream.range(1, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());

            Emulator.getGameEnvironment().getHabboManager().staffAlert(message + "\r\n-" + gameClient.getHabbo().getHabboInfo().getUsername());
            Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new NewConsoleMessageComposer(new Message(gameClient.getHabbo().getHabboInfo().getId(), -1, message)).compose(), "acc_staff_chat", gameClient);
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_staffalert.forgot_message"), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}
