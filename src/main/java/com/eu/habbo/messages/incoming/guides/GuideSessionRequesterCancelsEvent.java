package com.eu.habbo.messages.incoming.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.incoming.MessageHandler;

public class GuideSessionRequesterCancelsEvent extends MessageHandler {
    @Override
    public void handle() {
        GuideTour tour = Emulator.getGameEnvironment().getGuideManager().getGuideTourByNoob(this.client.getHabbo());

        if (tour != null) {
            tour.end();
            Emulator.getGameEnvironment().getGuideManager().endSession(tour);
        }
    }
}
