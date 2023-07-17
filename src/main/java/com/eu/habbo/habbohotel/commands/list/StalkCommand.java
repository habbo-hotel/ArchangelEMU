package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.GetGuestRoomResultComposer;

public class StalkCommand extends Command {
    public StalkCommand() {
        super("cmd_stalk");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getRoomUnit().getRoom() == null)
            return true;

        if (params.length >= 2) {
            Habbo habbo = getHabbo(params[1]);

            if (habbo == null) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_stalk.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (habbo.getRoomUnit().getRoom() == null) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_stalk.not_room"), params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (gameClient.getHabbo().getHabboInfo().getUsername().equals(habbo.getHabboInfo().getUsername())) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.generic.cmd_stalk.self"), params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (gameClient.getHabbo().getRoomUnit().getRoom() == habbo.getRoomUnit().getRoom()) {
                gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.generic.cmd_stalk.same_room"), params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }

            gameClient.sendResponse(new GetGuestRoomResultComposer(habbo.getRoomUnit().getRoom(), gameClient.getHabbo(), true, false));
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_stalk.forgot_username"), RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}
