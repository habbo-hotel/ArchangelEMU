package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.NoAuthMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.InitDiffieHandshakeComposer;

@NoAuthMessage
public class InitDiffieHandshakeEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new InitDiffieHandshakeComposer(
                this.client.getEncryption().getDiffie().getSignedPrime(),
                this.client.getEncryption().getDiffie().getSignedGenerator()));
    }

}
