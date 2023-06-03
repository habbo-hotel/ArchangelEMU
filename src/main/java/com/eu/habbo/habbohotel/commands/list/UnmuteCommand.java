package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class UnmuteCommand extends Command {
    public UnmuteCommand() {
        super("cmd_unmute");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 1) {
            gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_unmute.not_specified"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

        if (habbo == null) {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_unmute.not_found"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        if (!habbo.getHabboStats().allowTalk() || (habbo.getHabboInfo().getCurrentRoom() != null && habbo.getHabboInfo().getCurrentRoom().isMuted(habbo))) {
            if (!habbo.getHabboStats().allowTalk()) {
                habbo.unMute();
            }

            if (habbo.getHabboInfo().getCurrentRoom() != null && habbo.getHabboInfo().getCurrentRoom().isMuted(habbo)) {
                habbo.getHabboInfo().getCurrentRoom().muteHabbo(habbo, 1);
            }

            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.succes.cmd_unmute"), params[1]), RoomChatMessageBubbles.ALERT);
        } else {
            gameClient.getHabbo().whisper(replaceUser(getTextsValue("commands.error.cmd_unmute.not_muted"), params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        return true;
    }
}