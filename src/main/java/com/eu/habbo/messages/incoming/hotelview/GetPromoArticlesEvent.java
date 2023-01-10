package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.CommunityGoalHallOfFameMessageComposer;
import com.eu.habbo.messages.outgoing.hotelview.CurrentTimingCodeMessageComposer;
import com.eu.habbo.messages.outgoing.hotelview.PromoArticlesMessageComposer;

public class GetPromoArticlesEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new CurrentTimingCodeMessageComposer("2013-05-08 13:0", "gamesmaker"));
        this.client.sendResponse(new CommunityGoalHallOfFameMessageComposer(Emulator.getGameEnvironment().getHotelViewManager().getHallOfFame()));
        this.client.sendResponse(new PromoArticlesMessageComposer());
    }
}
