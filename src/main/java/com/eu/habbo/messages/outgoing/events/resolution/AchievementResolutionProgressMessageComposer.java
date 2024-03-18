package com.eu.habbo.messages.outgoing.events.resolution;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AchievementResolutionProgressMessageComposer extends MessageComposer {
        private final int stuffId;
        private final int achievementId;
        private final String achievementName;
        private final int currentProgress;
        private final int progressNeeded;
        private final int timeLeft;

        @Override
        protected ServerMessage composeInternal() {
            this.response.init(Outgoing.achievementResolutionProgressMessageComposer);
            this.response.appendInt(this.stuffId);
            this.response.appendInt(this.achievementId);
            this.response.appendString(this.achievementName);
            this.response.appendInt(this.currentProgress);
            this.response.appendInt(this.progressNeeded);
            this.response.appendInt(this.timeLeft);
            return this.response;
        }
    }
