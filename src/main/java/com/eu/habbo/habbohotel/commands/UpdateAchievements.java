package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UpdateAchievements extends Command {
    public UpdateAchievements() {
        super("cmd_update_achievements", Emulator.getTexts().getValue("commands.keys.cmd_update_achievements").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getAchievementManager().reload();
        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_achievements.updated"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}