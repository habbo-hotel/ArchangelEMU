package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CompleteDiffieHandshakeComposer extends MessageComposer {

    private final String publicKey;
    private final boolean clientEncryption;

    public CompleteDiffieHandshakeComposer(String publicKey) {
        this(publicKey, true);
    }


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.completeDiffieHandshakeComposer);
        this.response.appendString(this.publicKey);
        this.response.appendBoolean(this.clientEncryption);
        return this.response;
    }

}
