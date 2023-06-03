package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.IgnoreResultMessageComposer;

public class MuteCommand extends Command {
    public MuteCommand() {
        super("cmd_mute");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 1) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_mute.not_specified"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

        if (habbo == null) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_mute.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        } else {
            if (habbo == gameClient.getHabbo()) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_mute.self"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            int duration = Integer.MAX_VALUE;

            if (params.length == 3) {
                try {
                    duration = Integer.parseInt(params[2]);

                    if (duration <= 0)
                        throw new Exception("");
                } catch (Exception e) {
                    gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_mute.time"), RoomChatMessageBubbles.ALERT);
                    return true;
                }
            }

            habbo.mute(duration, false);

            if (habbo.getHabboInfo().getCurrentRoom() != null) {
                habbo.getHabboInfo().getCurrentRoom().sendComposer(new IgnoreResultMessageComposer(habbo, IgnoreResultMessageComposer.MUTED).compose()); //: RoomUserIgnoredComposer.UNIGNORED
            }

            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_mute.muted"), params[1]), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}
