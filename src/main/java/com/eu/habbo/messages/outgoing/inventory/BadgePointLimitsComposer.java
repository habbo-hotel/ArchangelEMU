package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;

public class BadgePointLimitsComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.BadgePointLimitsComposer);

        synchronized (Emulator.getGameEnvironment().getAchievementManager().getAchievements()) {
            THashMap<String, Achievement> achievements = Emulator.getGameEnvironment().getAchievementManager().getAchievements();

            this.response.appendInt(achievements.size());
            achievements.forEachValue(new TObjectProcedure<Achievement>() {
                @Override
                public boolean execute(Achievement achievement) {
                    BadgePointLimitsComposer.this.response.appendString((achievement.name.startsWith("ACH_") ? achievement.name.replace("ACH_", "") : achievement.name));
                    BadgePointLimitsComposer.this.response.appendInt(achievement.levels.size());

                    for (AchievementLevel level : achievement.levels.values()) {
                        BadgePointLimitsComposer.this.response.appendInt(level.level);
                        BadgePointLimitsComposer.this.response.appendInt(level.progress);
                    }

                    return true;
                }
            });
        }
        return this.response;
    }
}
