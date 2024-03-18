package com.eu.habbo.messages.outgoing.polls.infobus;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QuestionFinishedComposer extends MessageComposer {
    private final int no;
    private final int yes;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.questionFinishedComposer);
        this.response.appendInt(-1);
        this.response.appendInt(2);
        this.response.appendString("0");
        this.response.appendInt(this.no);
        this.response.appendString("1");
        this.response.appendInt(this.yes);
        return this.response;
    }
}