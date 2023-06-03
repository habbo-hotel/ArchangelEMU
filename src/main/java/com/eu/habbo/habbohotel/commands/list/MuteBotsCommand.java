package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class MuteBotsCommand extends Command {
    public MuteBotsCommand() {
        super("cmd_mute_bots");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        gameClient.getHabbo().getHabboStats().setIgnoreBots(!gameClient.getHabbo().getHabboStats().isIgnoreBots());
        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_mute_bots." + (gameClient.getHabbo().getHabboStats().isIgnoreBots() ? "ignored" : "unignored")), RoomChatMessageBubbles.ALERT);
        return true;
    }
}