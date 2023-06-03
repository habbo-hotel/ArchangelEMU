package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.habbohotel.catalog.ClubOffer;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.subscriptions.Subscription;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.PurchaseErrorMessageComposer;
import com.eu.habbo.messages.outgoing.catalog.PurchaseOKMessageComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListInvalidateComposer;
import com.eu.habbo.messages.outgoing.users.ActivityPointsMessageComposer;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;

public abstract class PurchaseEvent extends MessageHandler {
    protected void purchase(ClubOffer deal, int totalDays, int totalCredits, int totalDuckets) throws Exception {
        if (totalDays > 0) {
            if (this.client.getHabbo().getHabboInfo().getCurrencyAmount(deal.getPointsType()) < totalDuckets)
                return;

            if (this.client.getHabbo().getHabboInfo().getCredits() < totalCredits)
                return;

            if (!this.client.getHabbo().hasRight(Permission.ACC_INFINITE_CREDITS))
                this.client.getHabbo().giveCredits(-totalCredits);

            if (!this.client.getHabbo().hasRight(Permission.ACC_INFINITE_POINTS))
                this.client.getHabbo().givePoints(deal.getPointsType(), -totalDuckets);


            if(this.client.getHabbo().getHabboStats().createSubscription(Subscription.HABBO_CLUB, (totalDays * 86400)) == null) {
                this.client.sendResponse(new PurchaseErrorMessageComposer(PurchaseErrorMessageComposer.SERVER_ERROR).compose());
                throw new Exception("Unable to create or extend subscription");
            }

            if (totalCredits > 0)
                this.client.sendResponse(new CreditBalanceComposer(this.client.getHabbo()));

            if (totalDuckets > 0)
                this.client.sendResponse(new ActivityPointsMessageComposer(this.client.getHabbo()));

            this.client.sendResponse(new PurchaseOKMessageComposer(null));
            this.client.sendResponse(new FurniListInvalidateComposer());

            this.client.getHabbo().getHabboStats().run();
        }
    }
}
