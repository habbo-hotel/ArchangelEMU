package com.eu.habbo.messages.incoming.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guides.GuideSessionErrorMessageComposer;

public class GuideSessionCreateEvent extends MessageHandler {
    @Override
    public void handle() {
        int type = this.packet.readInt();
        String message = this.packet.readString();

        GuideTour activeTour = Emulator.getGameEnvironment().getGuideManager().getGuideTourByHabbo(this.client.getHabbo());

        if (activeTour != null) {
            this.client.sendResponse(new GuideSessionErrorMessageComposer(GuideSessionErrorMessageComposer.SOMETHING_WRONG_REQUEST));
            return;
        }

        GuideTour tour = new GuideTour(this.client.getHabbo(), message);
        tour.setStartTime(Emulator.getIntUnixTimestamp());

        Emulator.getGameEnvironment().getGuideManager().findHelper(tour);
    }
}
