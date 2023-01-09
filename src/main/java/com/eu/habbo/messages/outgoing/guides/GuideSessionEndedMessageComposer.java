package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuideSessionEndedMessageComposer extends MessageComposer {
    public static final int SOMETHING_WRONG = 0;
    public static final int HELP_CASE_CLOSED = 1;

    private final int errorCode;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guideSessionEndedMessageComposer);
        this.response.appendInt(this.errorCode); //?
        return this.response;
    }
}
