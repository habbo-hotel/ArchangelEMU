package com.eu.habbo.roleplay.messages.incoming.taxi;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.taxi.CallTaxiCommand;

public class CallTaxiEvent extends MessageHandler {
    @Override
    public void handle() {
        String roomID = this.packet.readInt().toString();
        new CallTaxiCommand().handle(this.client,new String[] {null, roomID});
    }
}