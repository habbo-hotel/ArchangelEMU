package com.eu.habbo.roleplay.messages.incoming.items;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.items.HotBarListItemsComposer;

public class HotBarListItemsEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new HotBarListItemsComposer(this.client.getHabbo()));
    }
}