package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;

public class UpdateUIFlagsEvent extends MessageHandler {
    @Override
    public void handle() {
        int flags = this.packet.readInt();

        this.client.getHabbo().getHabboStats().setUiFlags(flags);
        this.client.getHabbo().getHabboStats().run();
    }
}
