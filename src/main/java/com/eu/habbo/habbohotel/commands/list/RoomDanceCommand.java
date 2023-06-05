package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.messages.outgoing.rooms.users.DanceMessageComposer;

public class RoomDanceCommand extends Command {
    public RoomDanceCommand() {
        super("cmd_room_dance");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 2) {
            int danceId;

            try {
                danceId = Integer.parseInt(params[1]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_danceall.invalid_dance"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (danceId < 0 || danceId > 4) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_danceall.outside_bounds"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos().forEach(habbo -> {
                habbo.getRoomUnit().setDanceType(DanceType.values()[danceId]);
                habbo.getHabboInfo().getCurrentRoom().sendComposer(new DanceMessageComposer(habbo.getRoomUnit()).compose());
            });
        } else {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_danceall.no_dance"), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}
