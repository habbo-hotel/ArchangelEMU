package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.BonusRareInfoMessageComposer;

public class GetBonusRareInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new BonusRareInfoMessageComposer(this.client.getHabbo()));
    }
}
