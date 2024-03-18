package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.ClubGiftInfoComposer;

public class GetClubGiftInfo extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new ClubGiftInfoComposer(
            (int) Math.floor(this.client.getHabbo().getHabboStats().getTimeTillNextClubGift() / 86400.0),
            this.client.getHabbo().getHabboStats().getRemainingClubGifts(),
            (int) Math.floor(this.client.getHabbo().getHabboStats().getPastTimeAsClub() / 86400.0)
        ));
    }
}
