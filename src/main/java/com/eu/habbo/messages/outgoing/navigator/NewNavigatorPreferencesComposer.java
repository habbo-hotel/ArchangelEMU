package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.users.HabboNavigatorWindowSettings;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NewNavigatorPreferencesComposer extends MessageComposer {
    private final HabboNavigatorWindowSettings windowSettings;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.newNavigatorPreferencesComposer);
        this.response.appendInt(this.windowSettings.getX());
        this.response.appendInt(this.windowSettings.getY());
        this.response.appendInt(this.windowSettings.getWidth());
        this.response.appendInt(this.windowSettings.getHeight());
        this.response.appendBoolean(this.windowSettings.isOpenSearches());
        this.response.appendInt(0);
        return this.response;
    }
}
