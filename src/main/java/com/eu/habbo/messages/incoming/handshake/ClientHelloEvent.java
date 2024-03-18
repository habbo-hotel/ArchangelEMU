package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.NoAuthMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

@NoAuthMessage
public class ClientHelloEvent extends MessageHandler {

    @Override
    public void handle() {
        this.packet.readString();
    }
}
