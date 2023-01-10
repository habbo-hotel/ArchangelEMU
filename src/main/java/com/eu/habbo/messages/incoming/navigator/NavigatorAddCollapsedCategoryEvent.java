package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.habbohotel.navigation.DisplayMode;
import com.eu.habbo.messages.incoming.MessageHandler;

public class NavigatorAddCollapsedCategoryEvent extends MessageHandler {
    @Override
    public void handle() {
        String category = this.packet.readString();
        this.client.getHabbo().getHabboStats().getNavigatorWindowSettings().setDisplayMode(category, DisplayMode.COLLAPSED);
    }
}
