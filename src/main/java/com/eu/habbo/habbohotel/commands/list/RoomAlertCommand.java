package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.modtool.IssueCloseNotificationMessageComposer;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoomAlertCommand extends Command {
    public RoomAlertCommand() {
        super("cmd_room_alert");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        String message;

        if (params.length < 2) {
            return false;
        }
        message = IntStream.range(1, params.length).mapToObj(i -> params[i] + " ").collect(Collectors.joining());

        if (message.length() == 0) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_roomalert.empty"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null) {
            room.sendComposer(new IssueCloseNotificationMessageComposer(message).compose());
            return true;
        }

        return false;
    }
}