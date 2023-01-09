package com.eu.habbo.messages.outgoing.achievements;

import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AchievementUnlockedComposer extends MessageComposer {
    private final Habbo habbo;
    private final Achievement achievement;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboAchievementNotificationMessageComposer);

        AchievementLevel level = this.achievement.getLevelForProgress(this.habbo.getHabboStats().getAchievementProgress(this.achievement));
        this.response.appendInt(this.achievement.id);
        this.response.appendInt(level.getLevel());
        this.response.appendInt(144);
        this.response.appendString("ACH_" + this.achievement.name + level.getLevel());
        this.response.appendInt(level.getRewardAmount());
        this.response.appendInt(level.getRewardType());
        this.response.appendInt(0);
        this.response.appendInt(10);
        this.response.appendInt(21);
        this.response.appendString(level.getLevel() > 1 ? "ACH_" + this.achievement.name + (level.getLevel() - 1) : "");
        this.response.appendString(this.achievement.category.name());
        this.response.appendBoolean(true);
        return this.response;
    }
}
