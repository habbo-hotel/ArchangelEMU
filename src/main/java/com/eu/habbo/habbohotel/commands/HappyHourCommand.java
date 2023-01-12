package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.HabboBroadcastMessageComposer;

import java.util.Map;

public class HappyHourCommand extends Command {
    public HappyHourCommand() {
        super("cmd_happyhour", Emulator.getTexts().getValue("commands.keys.cmd_happyhour").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new HabboBroadcastMessageComposer("Happy Hour!"));

        Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()
                .forEach(habbo -> AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("HappyHour")));

        return true;
    }
}
