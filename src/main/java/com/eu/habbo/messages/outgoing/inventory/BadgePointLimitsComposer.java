package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;

public class BadgePointLimitsComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.badgePointLimitsComposer);

        synchronized (Emulator.getGameEnvironment().getAchievementManager().getAchievements()) {
            THashMap<String, Achievement> achievements = Emulator.getGameEnvironment().getAchievementManager().getAchievements();

            this.response.appendInt(achievements.size());
            achievements.forEachValue(achievement -> {
                BadgePointLimitsComposer.this.response.appendString((achievement.name.startsWith("ACH_") ? achievement.name.replace("ACH_", "") : achievement.name));
                BadgePointLimitsComposer.this.response.appendInt(achievement.levels.size());

                for (AchievementLevel level : achievement.levels.values()) {
                    BadgePointLimitsComposer.this.response.appendInt(level.getLevel());
                    BadgePointLimitsComposer.this.response.appendInt(level.getProgress());
                }

                return true;
            });
        }
        return this.response;
    }
}
