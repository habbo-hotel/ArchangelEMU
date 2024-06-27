package com.eu.habbo.roleplay.messages.incoming.device;

import com.eu.habbo.messages.incoming.MessageHandler;

public class InfoScreenSaveEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemID = this.packet.readInt();
        String content = this.packet.readString();
        this.client.getHabbo().shout("I SAVED STUFF");
    }
}