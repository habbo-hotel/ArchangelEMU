package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class MutePetsCommand extends Command {
    public MutePetsCommand() {
        super(null, Emulator.getTexts().getValue("commands.keys.cmd_mute_pets").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        gameClient.getHabbo().getHabboStats().setIgnorePets(!gameClient.getHabbo().getHabboStats().isIgnorePets());
        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_mute_pets." + (gameClient.getHabbo().getHabboStats().isIgnorePets() ? "ignored" : "unignored")), RoomChatMessageBubbles.ALERT);
        return true;
    }
}