package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.BundleDiscountRulesetMessageComposer;

public class GetBundleDiscountRulesetEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new BundleDiscountRulesetMessageComposer());
    }
}
