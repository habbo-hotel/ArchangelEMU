package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AchievementsScoreComposer extends MessageComposer {

    private final Habbo habbo;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.achievementsScoreComposer);
        this.response.appendInt(this.habbo.getHabboStats().getAchievementScore());
        return this.response;
    }
}
