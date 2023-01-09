package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WelcomeGiftStatusComposer extends MessageComposer {
    private final String email;
    private final boolean unknownB1;
    private final boolean unknownB2;
    private final int furniId;
    private final boolean unknownB3;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.welcomeGiftStatusComposer);
        this.response.appendString(this.email);
        this.response.appendBoolean(this.unknownB1);
        this.response.appendBoolean(this.unknownB2);
        this.response.appendInt(this.furniId);
        this.response.appendBoolean(this.unknownB3);
        return this.response;
    }
}