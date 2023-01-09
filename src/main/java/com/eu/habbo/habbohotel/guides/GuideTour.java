package com.eu.habbo.habbohotel.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;

public class GuideTour {
    @Getter
    private final Habbo noob;
    @Getter
    private final String helpRequest;
    private final THashSet<GuideChatMessage> sendMessages = new THashSet<>();
    private final THashSet<Integer> declinedHelpers = new THashSet<>();
    public int checkSum = 0;
    @Setter
    @Getter
    private Habbo helper;
    @Setter
    @Getter
    private int startTime;
    @Getter
    private int endTime;
    @Getter
    private boolean ended;
    @Getter
    private GuideRecommendStatus wouldRecommend = GuideRecommendStatus.UNKNOWN;

    public GuideTour(Habbo noob, String helpRequest) {
        this.noob = noob;
        this.helpRequest = helpRequest;

        AchievementManager.progressAchievement(this.noob, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GuideAdvertisementReader"));
    }

    public void finish() {
        //TODO Insert recommendation.
        //TODO Query messages.
    }

    public void addMessage(GuideChatMessage message) {
        this.sendMessages.add(message);
    }

    public void setWouldRecommend(GuideRecommendStatus wouldRecommend) {
        this.wouldRecommend = wouldRecommend;

        if (this.wouldRecommend == GuideRecommendStatus.YES) {
            AchievementManager.progressAchievement(this.getHelper(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("GuideRecommendation"));
        }
    }

    public void addDeclinedHelper(int userId) {
        this.declinedHelpers.add(userId);
    }

    public boolean hasDeclined(int userId) {
        return this.declinedHelpers.contains(userId);
    }

    public void end() {
        this.ended = true;
        this.endTime = Emulator.getIntUnixTimestamp();

        AchievementManager.progressAchievement(this.helper, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GuideEnrollmentLifetime"));
        AchievementManager.progressAchievement(this.helper, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GuideRequestHandler"));
        AchievementManager.progressAchievement(this.noob, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GuideRequester"));
    }

}
