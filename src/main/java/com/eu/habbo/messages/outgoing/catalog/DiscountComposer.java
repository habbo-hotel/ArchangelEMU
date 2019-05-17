package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class DiscountComposer extends MessageComposer
{
    public final static int MAXIMUM_ALLOWED_ITEMS = 100;
    public final static int DISCOUNT_BATCH_SIZE = 6;
    public final static int DISCOUNT_AMOUNT_PER_BATCH = 1;
    public final static int MINIMUM_DISCOUNTS_FOR_BONUS = 1;
    public final static int[] ADDITIONAL_DISCOUNT_THRESHOLDS = new int[]{40, 99};

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.DiscountComposer);

        this.response.appendInt(MAXIMUM_ALLOWED_ITEMS);
        this.response.appendInt(DISCOUNT_BATCH_SIZE);
        this.response.appendInt(DISCOUNT_AMOUNT_PER_BATCH);
        this.response.appendInt(MINIMUM_DISCOUNTS_FOR_BONUS);

        this.response.appendInt(ADDITIONAL_DISCOUNT_THRESHOLDS.length);
        for (int threshold : ADDITIONAL_DISCOUNT_THRESHOLDS) {
            this.response.appendInt(threshold);
        }

        return this.response;
    }
}
