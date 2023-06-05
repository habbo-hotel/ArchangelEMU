package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class MutePetsCommand extends Command {
    public MutePetsCommand() {
        super("cmd_mute_pets");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        gameClient.getHabbo().getHabboStats().setIgnorePets(!gameClient.getHabbo().getHabboStats().isIgnorePets());
        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_mute_pets." + (gameClient.getHabbo().getHabboStats().isIgnorePets() ? "ignored" : "unignored")), RoomChatMessageBubbles.ALERT);
        return true;
    }
}