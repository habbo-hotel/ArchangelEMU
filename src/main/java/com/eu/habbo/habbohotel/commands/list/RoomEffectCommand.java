package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class RoomEffectCommand extends Command {
    public RoomEffectCommand() {
        super("cmd_room_effect");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length < 2) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_roomeffect.no_effect"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        try {
            int effectId = Integer.parseInt(params[1]);

            if (effectId >= 0) {
                Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
                room.getHabbos().forEach(habbo -> room.giveEffect(habbo, effectId, -1));

            } else {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_roomeffect.positive"), RoomChatMessageBubbles.ALERT);
            }
            return true;
        } catch (Exception e) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_roomeffect.numbers_only"), RoomChatMessageBubbles.ALERT);
            return true;
        }
    }
}
