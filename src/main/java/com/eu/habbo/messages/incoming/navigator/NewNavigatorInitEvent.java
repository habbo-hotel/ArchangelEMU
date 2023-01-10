package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.*;

public class NewNavigatorInitEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new NewNavigatorPreferencesComposer(this.client.getHabbo().getHabboStats().getNavigatorWindowSettings()));
        this.client.sendResponse(new NavigatorMetaDataComposer());
        this.client.sendResponse(new NavigatorLiftedRoomsComposer());
        this.client.sendResponse(new CollapsedCategoriesComposer());
        this.client.sendResponse(new NavigatorSavedSearchesComposer(this.client.getHabbo().getHabboInfo().getSavedSearches()));
        this.client.sendResponse(new UserEventCatsComposer());
    }
}
