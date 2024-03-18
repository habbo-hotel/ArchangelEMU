package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.habbohotel.users.subscriptions.SubscriptionHabboClub;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ScrGetKickbackInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(SubscriptionHabboClub.calculatePayday(this.client.getHabbo().getHabboInfo()));
    }
}
