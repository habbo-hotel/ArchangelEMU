package com.eu.habbo.messages.incoming.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guides.GuideSessionRequesterRoomMessageComposer;

public class GuideSessionGetRequesterRoomEvent extends MessageHandler {
    @Override
    public void handle() {
        GuideTour tour = Emulator.getGameEnvironment().getGuideManager().getGuideTourByHelper(this.client.getHabbo());

        if (tour != null) {
            this.client.sendResponse(new GuideSessionRequesterRoomMessageComposer(tour.getNoob().getHabboInfo().getCurrentRoom()));
        }
    }
}
