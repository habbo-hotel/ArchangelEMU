package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.HabboBroadcastMessageComposer;

public class HappyHourCommand extends Command {
    public HappyHourCommand() {
        super("cmd_happyhour");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new HabboBroadcastMessageComposer("Happy Hour!"));

        Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()
                .forEach(habbo -> AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("HappyHour")));

        return true;
    }
}
