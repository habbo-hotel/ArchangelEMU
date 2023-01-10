package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.habbohotel.users.HabboNavigatorWindowSettings;
import com.eu.habbo.messages.incoming.MessageHandler;

public class SetNewNavigatorWindowPreferencesEvent extends MessageHandler {
    @Override
    public void handle() {
        HabboNavigatorWindowSettings windowSettings = this.client.getHabbo().getHabboStats().getNavigatorWindowSettings();

        windowSettings.setX(this.packet.readInt());
        windowSettings.setY(this.packet.readInt());

        windowSettings.setWidth(this.packet.readInt());
        windowSettings.setHeight(this.packet.readInt());

        boolean openSearches = this.packet.readBoolean();
        windowSettings.setOpenSearches(openSearches);

        int unknownVar = this.packet.readInt();
    }
}
